# MySQL连接问题解决指南

## 问题症状

```
ERROR 2002 (HY000): Can't connect to local MySQL server through socket '/var/run/mysqld/mysqld.sock' (2)
```

这个错误表示MySQL服务没有正常运行。

---

## 🔧 解决方案

### 方法一：使用修复脚本（推荐）⭐

```bash
cd /mnt/e/Code/GraduationProject/scripts
sudo ./fix-mysql.sh
```

这个脚本会自动：
- 检查MySQL状态
- 修复权限问题
- 重启MySQL服务
- 测试连接

---

### 方法二：手动修复步骤

#### 步骤1：检查MySQL服务状态

```bash
sudo systemctl status mysql
```

可能看到的状态：
- `inactive (dead)` - 服务未运行
- `failed` - 服务启动失败
- `activating` - 正在启动中

#### 步骤2：查看MySQL错误日志

```bash
sudo tail -50 /var/log/mysql/error.log
```

常见错误类型：
1. **权限问题**: `Permission denied`
2. **端口占用**: `Address already in use`
3. **磁盘空间不足**: `No space left on device`
4. **配置文件错误**: `unknown variable`
5. **数据文件损坏**: `InnoDB: corrupted`

#### 步骤3：停止MySQL服务

```bash
sudo systemctl stop mysql
```

#### 步骤4：修复权限

```bash
sudo chown -R mysql:mysql /var/lib/mysql
sudo chmod -R 755 /var/lib/mysql
```

#### 步骤5：重启MySQL

```bash
sudo systemctl start mysql
```

#### 步骤6：验证服务状态

```bash
sudo systemctl status mysql
```

应该看到 `active (running)`

#### 步骤7：测试连接

```bash
# 方式1：无密码登录（如果没设置密码）
sudo mysql

# 方式2：使用密码登录
mysql -u root -p
```

---

## 🔍 常见问题诊断

### 问题1：MySQL服务启动失败

**症状**：
```
Job for mysql.service failed because the control process exited with error code.
```

**解决方案**：

```bash
# 1. 检查错误日志
sudo tail -100 /var/log/mysql/error.log

# 2. 检查配置文件
sudo mysql --help --verbose | grep "Default options" -A 1

# 3. 测试配置文件
sudo mysqld --verbose --help 2>&1 | grep "Error"

# 4. 重新初始化（慎用，会删除数据！）
# sudo rm -rf /var/lib/mysql/*
# sudo mysqld --initialize --user=mysql
```

### 问题2：端口3306被占用

**检查端口占用**：
```bash
sudo lsof -i :3306
# 或
sudo netstat -tuln | grep 3306
```

**解决方案**：
```bash
# 结束占用端口的进程
sudo kill -9 [PID]

# 或修改MySQL端口
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
# 修改: port = 3307
```

### 问题3：权限问题

**症状**：
```
[ERROR] InnoDB: Operating system error number 13 in a file operation.
[ERROR] InnoDB: The error means mysqld does not have the access rights
```

**解决方案**：
```bash
# 修复所有权
sudo chown -R mysql:mysql /var/lib/mysql
sudo chown -R mysql:mysql /var/run/mysqld
sudo chown -R mysql:mysql /var/log/mysql

# 修复权限
sudo chmod -R 755 /var/lib/mysql
sudo chmod 755 /var/run/mysqld
```

### 问题4：磁盘空间不足

**检查磁盘空间**：
```bash
df -h /var/lib/mysql
```

**解决方案**：
```bash
# 清理日志文件
sudo rm /var/log/mysql/error.log.*

# 清理旧的二进制日志
sudo mysql -e "PURGE BINARY LOGS BEFORE NOW();"

# 清理系统临时文件
sudo apt clean
```

### 问题5：socket文件不存在

**症状**：
```
Can't connect to local MySQL server through socket '/var/run/mysqld/mysqld.sock'
```

**解决方案**：
```bash
# 1. 创建socket目录
sudo mkdir -p /var/run/mysqld
sudo chown mysql:mysql /var/run/mysqld
sudo chmod 755 /var/run/mysqld

# 2. 重启MySQL
sudo systemctl restart mysql

# 3. 检查socket文件
ls -la /var/run/mysqld/mysqld.sock
```

---

## 🔐 重置MySQL Root密码

如果您忘记了root密码或需要重新设置：

### 方法1：使用sudo直接登录

```bash
# Ubuntu/Debian系统可以直接使用sudo
sudo mysql

# 在MySQL提示符中：
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
EXIT;
```

### 方法2：安全模式重置密码

```bash
# 1. 停止MySQL
sudo systemctl stop mysql

# 2. 以安全模式启动
sudo mysqld_safe --skip-grant-tables &

# 3. 登录MySQL（无需密码）
mysql -u root

# 4. 重置密码
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
EXIT;

# 5. 结束安全模式进程
sudo killall mysqld_safe
sudo killall mysqld

# 6. 正常启动MySQL
sudo systemctl start mysql

# 7. 测试新密码
mysql -u root -p
# 输入密码: root
```

---

## ✅ 验证MySQL正常工作

执行以下命令确认MySQL正常：

```bash
# 1. 检查服务状态
sudo systemctl status mysql
# 应该看到: active (running)

# 2. 检查端口监听
sudo netstat -tuln | grep 3306
# 应该看到: 0.0.0.0:3306

# 3. 检查进程
ps aux | grep mysqld
# 应该看到MySQL进程

# 4. 测试连接
mysql -u root -p
# 输入密码后应该能登录

# 5. 在MySQL中测试
mysql -u root -p -e "SELECT VERSION(); SHOW DATABASES;"
```

---

## 📋 MySQL启动检查清单

完成以下检查，确保MySQL可以正常启动：

- [ ] 磁盘空间充足（>10%可用）
- [ ] /var/lib/mysql 目录权限正确（mysql:mysql）
- [ ] /var/run/mysqld 目录存在且权限正确
- [ ] 端口3306未被占用
- [ ] 配置文件语法正确
- [ ] 错误日志中无严重错误
- [ ] MySQL服务状态为 active (running)
- [ ] 可以使用mysql命令连接

---

## 🚀 初始化项目数据库

MySQL正常启动后，执行以下步骤初始化项目数据库：

```bash
# 1. 登录MySQL
mysql -u root -p
# 输入密码: root

# 2. 执行初始化脚本
source /mnt/e/Code/GraduationProject/scripts/init-mysql.sql;

# 3. 验证数据库创建
SHOW DATABASES;
# 应该看到: power_diagnosis

# 4. 查看表
USE power_diagnosis;
SHOW TABLES;
# 应该看到6张表

# 5. 检查用户数据
SELECT * FROM sys_user;
# 应该看到admin用户

# 6. 退出
EXIT;
```

---

## 🔄 完整的MySQL重装步骤（最后手段）

如果所有方法都失败，可以完全重装MySQL：

```bash
# ⚠️ 警告：这会删除所有MySQL数据！

# 1. 完全卸载MySQL
sudo systemctl stop mysql
sudo apt purge mysql-server mysql-client mysql-common mysql-server-core-* mysql-client-core-*
sudo rm -rf /etc/mysql /var/lib/mysql
sudo apt autoremove
sudo apt autoclean

# 2. 重新安装
sudo apt update
sudo apt install -y mysql-server

# 3. 启动服务
sudo systemctl start mysql
sudo systemctl enable mysql

# 4. 检查状态
sudo systemctl status mysql

# 5. 设置root密码
sudo mysql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
EXIT;

# 6. 初始化数据库
mysql -u root -p < /mnt/e/Code/GraduationProject/scripts/init-mysql.sql
```

---

## 📞 获取更多帮助

如果问题仍未解决：

1. **查看完整日志**：
   ```bash
   sudo journalctl -u mysql -n 100 --no-pager
   ```

2. **检查系统日志**：
   ```bash
   sudo tail -100 /var/log/syslog | grep mysql
   ```

3. **在线搜索错误信息**：
   - 复制错误日志中的关键错误信息
   - 在Google或Stack Overflow搜索

4. **参考官方文档**：
   - MySQL官方文档: https://dev.mysql.com/doc/
   - Ubuntu MySQL指南: https://ubuntu.com/server/docs/databases-mysql

---

## 🎯 快速命令参考

```bash
# 启动MySQL
sudo systemctl start mysql

# 停止MySQL
sudo systemctl stop mysql

# 重启MySQL
sudo systemctl restart mysql

# 查看状态
sudo systemctl status mysql

# 查看日志
sudo tail -f /var/log/mysql/error.log

# 登录MySQL
mysql -u root -p

# 以root身份登录（Ubuntu）
sudo mysql

# 检查端口
sudo netstat -tuln | grep 3306

# 检查进程
ps aux | grep mysql
```

---

**提示**：大多数MySQL启动问题都可以通过修复脚本解决。如果还有问题，请按照上述步骤逐一排查。
