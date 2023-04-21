## Maven私服配置

配置公司maven私服地址：



1、配置hosts文件，

`windows`文件路径：`C:\Windows\System32\drivers\etc\hosts`

`linux/mac`文件路径：`/etc/hosts`

防止服务器IP变更导致无法正常访问

``` properties
192.168.0.155 gitlab.yitong.com.cn
192.168.0.152 nexus.yitong.com.cn
```



2、配置maven settings文件

配置仓库访问地址：

```xml
<profiles>
	<profile>
		<id>nexus</id>
		<repositories>
			<repository>
				<id>nexus</id>
				<url>http://nexus.yitong.com.cn:8081/repository/public/</url>
				<releases>
					<enabled>true</enabled>
				</releases>
				<snapshots>
					<enabled>true</enabled>
				</snapshots>
			</repository>
		</repositories>
		<pluginRepositories>
			<pluginRepository>
				<id>nexus</id>
				<url>http://nexus.yitong.com.cn:8081/repository/public/</url>
				<releases>
					<enabled>true</enabled>
				</releases>
				<snapshots>
					<enabled>true</enabled>
				</snapshots>
			</pluginRepository>
		</pluginRepositories>
	</profile>
</profiles>

<activeProfiles>
	<activeProfile>nexus</activeProfile>
</activeProfiles>

```

