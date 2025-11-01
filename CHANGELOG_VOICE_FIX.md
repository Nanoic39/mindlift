# 语音识别接口修复记录

## 修复日期
2025-10-28

## 问题描述

原语音转换文本接口 `/ai/video2text` 存在以下问题：

1. **JAR包资源加载失败**：使用 `resource.getFile()` 在JAR包中无法正常工作
2. **资源泄漏**：没有正确关闭音频流和临时文件
3. **错误处理不完善**：异常处理过于简单，错误信息不明确
4. **缺少文件验证**：没有验证上传文件是否为空
5. **缺少文档**：没有使用说明和配置文档

## 修复内容

### 1. 核心代码修复

**文件**: `src/main/java/cn/dcstd/web/mindlift3/controller/AIController.java`

#### 修改点：

##### a. 添加必要的导入
```java
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
```

##### b. 重写 `video2text` 方法
- ✅ 添加文件空值检查
- ✅ 使用临时文件处理上传的音频
- ✅ 实现完善的资源清理机制（try-finally）
- ✅ 改进错误处理和日志输出
- ✅ 正确关闭 Recognizer 对象

##### c. 新增 `getModelPath()` 方法
支持两种模型加载方式：
1. **外部路径**（生产环境推荐）：通过 `-Dvosk.model.path` JVM参数指定
2. **资源目录**（开发环境）：从 `src/main/resources/vosk/` 自动加载

##### d. 新增 `extractModelFromJar()` 方法
占位方法，提示用户使用外部模型路径（因为从JAR提取大型模型复杂且低效）

### 2. 文档和脚本

#### 新增文件：

1. **VOSK_SETUP.md**
   - Vosk模型配置完整说明
   - 开发和生产环境使用指南
   - API接口文档
   - 音频格式要求和转换方法
   - 常见问题解答
   - 性能优化建议

2. **start.bat** / **start.sh**
   - 生产环境启动脚本（使用外部模型）
   - 自动检查模型路径和JAR文件
   - Windows和Linux/Mac双平台支持

3. **start-dev.bat** / **start-dev.sh**
   - 开发环境快速启动脚本
   - 使用项目内置模型

4. **test-voice-api.md**
   - API测试完整指南
   - 多种测试方法（cURL, Postman, Python, Node.js）
   - 测试音频准备方法
   - 调试技巧和常见问题排查
   - 性能测试建议

5. **test-api.ps1**
   - PowerShell自动化测试脚本
   - 一键测试语音识别API
   - 友好的输出格式和错误提示

6. **CHANGELOG_VOICE_FIX.md**
   - 本文档，详细记录修复内容

## 技术细节

### 资源加载方案对比

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| ClassPath + getFile() | 简单直接 | JAR包中失败 | 仅开发环境 |
| 外部路径（推荐） | 灵活可靠 | 需要配置 | 生产环境 |
| JAR提取到临时目录 | 自包含 | 启动慢，占用空间 | 不推荐 |

### 修复后的工作流程

```
1. 接收上传的音频文件（MultipartFile）
   ↓
2. 验证文件是否为空
   ↓
3. 获取模型路径（外部路径优先）
   ↓
4. 加载Vosk模型
   ↓
5. 创建临时文件保存音频
   ↓
6. 读取音频流（AudioInputStream）
   ↓
7. 创建识别器（Recognizer）
   ↓
8. 处理音频数据（分块读取）
   ↓
9. 获取最终识别结果
   ↓
10. 清理资源（关闭流、删除临时文件）
    ↓
11. 返回结果
```

## 使用方法

### 开发环境

```bash
# Windows
start-dev.bat

# Linux/Mac
chmod +x start-dev.sh
./start-dev.sh
```

### 生产环境

```bash
# Windows
# 1. 修改 start.bat 中的模型路径
# 2. 运行
start.bat

# Linux/Mac
# 1. 修改 start.sh 中的模型路径
# 2. 添加执行权限并运行
chmod +x start.sh
./start.sh
```

### 测试接口

```bash
# Windows PowerShell
.\test-api.ps1

# 或使用curl
curl -X POST http://localhost:8080/ai/video2text -F "file=@test-audio.wav"
```

## 配置参数

### JVM参数

| 参数 | 说明 | 示例 |
|------|------|------|
| vosk.model.path | Vosk模型目录路径 | -Dvosk.model.path=/opt/vosk-models/vosk-model-small-cn-0.22 |

### 环境变量（可选）

可以设置 `JAVA_OPTS` 环境变量：

```bash
# Linux/Mac
export JAVA_OPTS="-Dvosk.model.path=/opt/vosk-models/vosk-model-small-cn-0.22"

# Windows
set JAVA_OPTS=-Dvosk.model.path=D:\vosk-models\vosk-model-small-cn-0.22
```

## 测试结果

### 测试环境
- 操作系统：Windows 10
- Java版本：22
- Spring Boot版本：3.4.2
- Vosk版本：0.3.32

### 功能测试
- ✅ 开发环境启动正常
- ✅ 生产环境（外部模型）启动正常
- ✅ WAV文件识别正常
- ✅ 空文件验证正常
- ✅ 错误处理正常
- ✅ 资源清理正常

## 已知限制

1. **音频格式**：仅支持WAV格式，其他格式需要先转换
2. **采样率**：建议16000Hz，不同采样率可能影响识别准确度
3. **模型语言**：当前模型仅支持中文
4. **文件大小**：建议不超过100MB
5. **并发处理**：当前实现每次都加载模型，高并发场景需要优化

## 后续优化建议

### 高优先级
1. 实现模型单例加载，避免重复加载
2. 添加音频格式自动转换（集成FFmpeg）
3. 实现异步处理，支持大文件

### 中优先级
1. 添加识别结果缓存
2. 支持实时流式识别
3. 添加识别进度回调

### 低优先级
1. 支持多语言模型切换
2. 添加识别准确度评分
3. 实现模型热更新

## 回归测试检查清单

在部署前请确认：

- [ ] 代码编译无错误
- [ ] 单元测试通过
- [ ] API接口可正常访问
- [ ] 模型文件配置正确
- [ ] 测试音频识别成功
- [ ] 错误处理符合预期
- [ ] 日志输出清晰
- [ ] 资源清理正常（无内存泄漏）
- [ ] 文档已更新

## 相关资源

- [Vosk官网](https://alphacephei.com/vosk/)
- [Vosk模型下载](https://alphacephei.com/vosk/models)
- [FFmpeg官网](https://ffmpeg.org/)
- [Spring Boot文档](https://spring.io/projects/spring-boot)

## 联系方式

如有问题或建议，请查看：
- 应用日志文件
- VOSK_SETUP.md 配置文档
- test-voice-api.md 测试文档

