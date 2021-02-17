# demojava8

#### 项目介绍
进行java8相关一些demo功能的



# 关于Git提交规范

**简介：** 自古至今，无规矩不成方圆。 Git提交也有其规范，业内做的比较好的，比较具有参考价值的就是Angular的提交。 
Angular提交规范: ():
 #header 
 // 空一行
  // 空一行 格式讲解 Header Header部分只有一行，
  包括三个字段：type（必需）、scope（可选）和subject（必需）。

自古至今，无规矩不成方圆。

Git提交也有其规范，业内做的比较好的，比较具有参考价值的就是Angular的提交。

Angular提交规范:

```
<type>(<scope>): <subject> #header
// 空一行
<body>
// 空一行
<footer> 
```

## 格式讲解

### Header

Header部分只有一行，包括三个字段：type（必需）、scope（可选）和subject（必需）。

 

总的来说，关键就是header这部分，至于<body>和<footer>可省略

例如:

```
feat:新增财务报表
```

 

#### type

用于说明本次commit的类别，只允许使用下面7个标识

- `feat`：新功能（feature）
- `fix`：修补bug
- `docs`：文档（documentation）
- `style`： 格式（不影响代码运行的变动）
- `refactor`：重构（即不是新增功能，也不是修改bug的代码变动）
- `test`：增加测试
- `chore`：构建过程或辅助工具的变动



注意:如果type为feat和fix，则该 commit 将肯定出现在 Change log 之中。其他情况（docs、chore、style、refactor、test）由你决定，要不要放入 Change log，建议是不要。

 

#### scope

用于说明 commit 影响的范围，比如数据层、控制层、视图层等等，视项目不同而不同。

#### subject

是 commit 目的的简短描述，不超过50个字符。

```
以动词开头，使用第一人称现在时，比如change，而不是changed或changes
第一个字母小写
结尾不加句号（.）
```

 