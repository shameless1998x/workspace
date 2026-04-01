# Word 文档下载功能 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为当前 Spring Boot 项目增加一个可下载 `hello.docx` 的接口，并生成一份可直接打开的示例 Word 文件。

**Architecture:** 在 `WordController` 中直接使用 Apache POI 于内存中创建 `.docx` 文档，写入 `hello` 段落后返回字节数组。测试层使用 `MockMvc` 校验下载接口的状态码、响应头、内容类型和返回体非空，同时用一个小型 Java 生成器生成项目内示例文档。

**Tech Stack:** Java 17, Spring Boot 3.2.4, spring-boot-starter-web, spring-boot-starter-test, Apache POI poi-ooxml, Maven Wrapper

---

## 文件结构

- 修改：`pom.xml`
  - 增加 Apache POI `poi-ooxml` 依赖
- 新建：`src/main/java/com/example/orderservice/WordController.java`
  - 提供 `GET /word/download` 下载接口
- 新建：`src/test/java/com/example/orderservice/WordControllerTest.java`
  - 使用 `MockMvc` 验证下载接口行为
- 新建：`src/test/java/com/example/orderservice/HelloDocxGenerator.java`
  - 生成项目中的示例 `hello.docx`
- 新建：`docs/generated/hello.docx`
  - 示例 Word 文档产物

### Task 1: 增加 Word 生成依赖

**Files:**
- Modify: `pom.xml:50-80`
- Test: `./mvnw test`

- [ ] **Step 1: 在 `pom.xml` 中增加失败前置检查用例所需依赖声明位置**

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.5</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-loadbalancer</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-bootstrap</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
</dependencies>
```

- [ ] **Step 2: 运行测试确认此时尚未实现接口时相关测试会失败或缺失**

Run: `./mvnw -q -Dtest=WordControllerTest test`
Expected: FAIL，提示 `No tests matching pattern` 或 `WordControllerTest` 不存在

- [ ] **Step 3: 保存依赖改动**

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

- [ ] **Step 4: 运行完整测试确认依赖引入后项目仍可编译**

Run: `./mvnw test`
Expected: PASS，现有测试通过

### Task 2: 实现 Word 下载接口

**Files:**
- Create: `src/main/java/com/example/orderservice/WordController.java`
- Test: `src/test/java/com/example/orderservice/WordControllerTest.java`

- [ ] **Step 1: 先写失败的控制器测试**

```java
package com.example.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDownloadHelloDocx() throws Exception {
        mockMvc.perform(get("/word/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=hello.docx"))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .andExpect(result -> {
                    byte[] body = result.getResponse().getContentAsByteArray();
                    if (body.length == 0) {
                        throw new AssertionError("响应体不应为空");
                    }
                });
    }
}
```

- [ ] **Step 2: 运行单测确认接口尚未实现时失败**

Run: `./mvnw -q -Dtest=WordControllerTest test`
Expected: FAIL，状态码不是 200 或映射不存在

- [ ] **Step 3: 编写最小可用实现**

```java
package com.example.orderservice;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/word")
public class WordController {

    private static final String DOCX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadHelloDocx() throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("hello");
            document.write(outputStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hello.docx")
                    .contentType(MediaType.parseMediaType(DOCX_CONTENT_TYPE))
                    .body(outputStream.toByteArray());
        }
    }
}
```

- [ ] **Step 4: 运行单测确认接口通过**

Run: `./mvnw -q -Dtest=WordControllerTest test`
Expected: PASS

- [ ] **Step 5: 运行完整测试确认无回归**

Run: `./mvnw test`
Expected: PASS

### Task 3: 生成示例 Word 文档

**Files:**
- Create: `src/test/java/com/example/orderservice/HelloDocxGenerator.java`
- Create: `docs/generated/hello.docx`
- Test: `docs/generated/hello.docx`

- [ ] **Step 1: 编写生成示例文件的 Java 程序**

```java
package com.example.orderservice;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class HelloDocxGenerator {

    public static void main(String[] args) throws IOException {
        Path output = Path.of("docs/generated/hello.docx");
        Files.createDirectories(output.getParent());

        try (XWPFDocument document = new XWPFDocument();
             OutputStream outputStream = Files.newOutputStream(output)) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("hello");
            document.write(outputStream);
        }
    }
}
```

- [ ] **Step 2: 运行生成器创建示例文档**

Run: `./mvnw -q -DskipTests test-compile exec:java -Dexec.mainClass=com.example.orderservice.HelloDocxGenerator -Dexec.classpathScope=test`
Expected: PASS，并生成 `docs/generated/hello.docx`

- [ ] **Step 3: 验证文件已生成**

Run: `ls -l docs/generated/hello.docx`
Expected: 输出该文件路径与非零大小

- [ ] **Step 4: 运行完整测试再次确认项目状态正常**

Run: `./mvnw test`
Expected: PASS
