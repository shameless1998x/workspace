# Word 文档下载设计

## 目标
在当前 Spring Boot 项目中新增一个 Controller，用于提供一个可下载的 Word 文档接口。访问该接口时，服务端动态生成一个内容为 `hello` 的 Word 文档并返回给客户端下载。

## 推荐方案
使用 Apache POI 在内存中动态生成 `.docx` 文档，然后由 Controller 以附件下载的方式返回。

### 选择这个方案的原因
- 生成的是真正的 Word `.docx` 文件
- 和当前项目已有的 Spring Boot Controller 写法一致
- 后续如果要扩展标题、段落、表格、样式，改动成本较低

## 接口设计
- Controller：`WordController`
- 接口：`GET /word/download`
- 响应类型：`application/vnd.openxmlformats-officedocument.wordprocessingml.document`
- 下载文件名：`hello.docx`

## 数据流
1. 客户端请求 `/word/download`
2. Controller 在内存中创建一个 Word 文档对象
3. Controller 写入一个内容为 `hello` 的段落
4. Controller 将文档序列化为字节数组
5. Controller 设置附件下载响应头并返回文件内容

## 依赖
需要在 `pom.xml` 中增加 Apache POI 的 `poi-ooxml` 依赖，用于生成 `.docx` 文档。

## 异常处理
该功能较简单，可直接在方法内完成文档生成。如果写出文档失败，则交由 Spring 按照服务端异常处理即可，不额外增加抽象层。

## 测试设计
- 增加针对 `GET /word/download` 的 Spring MVC 测试
- 验证响应状态码为 200
- 验证 `Content-Disposition` 中包含 `hello.docx`
- 验证响应 `Content-Type` 为 docx 对应的 MIME 类型
- 验证响应体非空

## 附加产物
除接口实现外，再额外生成一份示例文件 `hello.docx`，方便直接打开查看效果。
