# MyBot
MyBot是一个基于Kotlin和Mirai开发的QQ机器人项目，使用命令行形式进行交互

目前接入的wiki:
- 星露谷
- 星露谷SVE
- 泰拉瑞亚
- 泰拉瑞亚灾厄

目前接入的AI:
- DeepSeek
- OpenAI
## 特性

- 使用 Kotlin 编写
- 配置管理
- 群组管理
- AI回复
- 游戏WIKI查询

## 快速开始

1. 克隆项目
```bash
git clone https://github.com/kaer-nk/MyBot.git
```

2. 配置
- 复制 `src/main/resources/config.json.example` 到同目录下的 `config.json`
- 修改 `config.json` 中的配置信息

3. 运行
```bash
./gradlew run
```

## 配置说明

在 `config.json` 中配置以下信息：
- qq：机器人QQ号
- password：密码
- groups：允许的群组列表
- 各种AI的配置信息

## 开源许可

本项目采用 MIT 许可证，详情请见 [LICENSE](LICENSE) 文件。