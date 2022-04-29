# Transaction

![img.png](E:\codeDemo\spring-transaction\README.assets\img.png)

## 什么是事务

事务是逻辑上的一组操作，要么都执行，要么都不执行。

事务最经典也经常被拿出来说例子就是转账了。假如小明要给小红转账 1000 元，这个转账会涉及到两个关键操作就是：将小明的余额减少 1000 元，将小红的余额增加 1000 元。万一在这两个操作之间突然出现错误比如银行系统崩溃，导致小明余额减少而小红的余额没有增加，这样就不对了。事务就是保证这两个关键操作要么都成功，要么都要失败。

## 事务的`ACID`
1. 原子性： 事务是最小的执行单位，不允许分割。事务的原子性确保动作要么全部完成，要么完全不起作用；
2. 一致性： 执行事务前后，数据保持一致，例如转账业务中，无论事务是否成功，转账者和收款人的总额应该是不变的；
3. 隔离性： 并发访问数据库时，一个用户的事务不被其他事务所干扰，各并发事务之间数据库是独立的；
4. 持久性： 一个事务被提交之后。它对数据库中数据的改变是持久的，即使数据库发生故障也不应该对其有任何影响。

## `JDBC`中手动事务的实现

### 基础版本

手动获取数据源的连接，将提交方式设置为手动，出现异常手动回滚。

```java
@Autowired
JdbcTemplate jdbcTemplate;

@Autowired
DataSource dataSource;

@Override
//@Transactional(rollbackFor = Exception.class)
public void insert() throws SQLException {

    Connection connection = dataSource.getConnection();
    connection.setAutoCommit(false);
    Statement statement = connection.createStatement();
    try {
        String s = UUID.randomUUID().toString().substring(0, 5);

        //配合spring事务
        //jdbcTemplate.execute("insert into user(nickname) values('" + s + "')");

        //手动控制
        statement.execute("insert into user(nickname) values('" + s + "')");
        //int a = 12 / 0;

        connection.commit();
    } catch (Exception e) {
        System.out.println("异常"+e.getMessage());
        connection.rollback();
    }finally {
        //关闭是放回连接池还是关闭java和mysql的连接
        //不一定，连接池不同，可能不同，一般是放回连接池
        connection.close();
    }
}
```

### 改进

将`JDBCTemplate`抽离出来,将无用的操作封装到`MyJDBCTemplate`中，`serveice`中只关心业务逻辑

```java
public void execute(String sql) throws SQLException {

    Connection connection = dataSource.getConnection();
    connection.setAutoCommit(false);
    Statement statement = connection.createStatement();
    try {
        statement.execute(sql);
        connection.commit();
    } catch (Exception e) {
        System.out.println("异常" + e.getMessage());
        connection.rollback();
    } finally {
        //关闭是放回连接池还是关闭java和mysql的连接
        //不一定，连接池不同，可能不同，一般是放回连接池
        connection.close();
    }
}

public void insert() throws SQLException {
        myJdbcTemplate.execute("insert into user(nickname) values('" + UUID.randomUUID().toString().substring(0, 5) + "')");
        myJdbcTemplate.execute("insert into user(nickname) values('" + UUID.randomUUID().toString().substring(0, 5) + "')");
        //int a = 10 / 0;
}
```

### QA——两条`sql`语句如何保证在一个连接内

#### `ThreadLocal`

[面试官：小伙子，听说你看过ThreadLocal源码？（万字图文深度解析ThreadLocal）](面试官：小伙子，听说你看过ThreadLocal源码？（万字图文深度解析ThreadLocal）)

### 通过事务管理

```java
@Autowired
DataSource dataSource;

//保证在一个线程中拿到的连接时同一个连接
ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();


public Connection getConnection() throws SQLException {
    if (connectionThreadLocal.get() != null) {
        return connectionThreadLocal.get();
    } else {
        connectionThreadLocal.set(dataSource.getConnection());
    }
    return connectionThreadLocal.get();
}
```

```java
@Autowired
MyTransactionManager myTransactionManager;

public void execute(String sql) throws SQLException {
    Connection connection = myTransactionManager.getConnection();
    Statement statement = connection.createStatement();
    statement.execute(sql);
}
```

## 通过aop实现一个简单的事务

```java
@Autowired
MyTransactionManager transactionManager;

@Around("@annotation(MyTransaction)")
public Object doTransaction(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    Connection connection = transactionManager.getConnection();
    connection.setAutoCommit(false);
    System.out.println("事务开始");
    try {
        Object proceed = proceedingJoinPoint.proceed();
        connection.commit();
        System.out.println("事务提交");
        return proceed;
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("事务回滚");
        connection.rollback();
    } finally {
        connection.close();
    }
    return null;
}
```

