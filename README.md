# 勇
`勇`[漢字] → `ゆう`[平仮名] → `YuU`[ローマ字]

## Maven
> 1.修改Maven配置(`$HOME/.m2/settings.xml`)
> ```xml
> <!-- 排除仓库:jitpack.io -->
> <mirrors>
>   <!-- ... -->
>   <mirror>
>     <!-- ... -->
>     <mirrorOf>*,!jitpack.io</mirrorOf>
>   </mirror>
> </mirrors>
> ```

> 2.删除本地文件
> ```bash
> rm -rf $HOME/.m2/repository/com/github/wanjune
> ```

> 3.工程中添加引用
> ```xml
> <!-- 仓库: jitpack.io -->
> <repositories>
>     <repository>
>         <id>jitpack.io</id>
>         <url>https://jitpack.io</url>
>     </repository>
> </repositories>
> ```
> ```xml
> <!-- 勇: Spring-boot增强工具 -->
> <dependency>
>     <groupId>com.github.wanjune</groupId>
>     <artifactId>yuu</artifactId>
>     <version>${yuu.version}</version>
> </dependency>
> ```
