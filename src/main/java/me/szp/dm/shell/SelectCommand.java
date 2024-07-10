package me.szp.dm.shell;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author szp
 * @since 2024/6/13 15:38
 */
@ShellComponent
@RequiredArgsConstructor
public class SelectCommand {

    private final DataSource dataSource;

    private final Environment environment;

    @ShellMethod("auth")
    public void auth(@ShellOption String username, String password) {

        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(String.format("jdbc:dm://%s:%s",
                environment.getProperty("spring.datasource.host"),
                environment.getProperty("spring.datasource.port")
        ));
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName("dm.jdbc.driver.DmDriver");

        // setDataSource(druidDataSource);
    }


    @ShellMethod("查询")
    public String select(@ShellOption(arity = 9999, defaultValue = "") String... cmd) {
        long start = System.currentTimeMillis();

        if (cmd.length == 0) {
            return "";
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Map<String, Object>> resultMap = jdbcTemplate.queryForList("select " + String.join(" ", cmd));
        System.out.println(formatAsTable(resultMap));
        return String.format("查询到%s条记录,耗时:%sms", resultMap.size(), System.currentTimeMillis() - start);
    }

    @ShellMethod("update")
    public String update(@ShellOption(arity = 9999, defaultValue = "") String... cmd) {
        if (cmd.length == 0) {
            return "";
        }
        long start = System.currentTimeMillis();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int update = jdbcTemplate.update("update " + String.join(" ", cmd));
        return String.format("影响了%s行,耗时:%sms", update, System.currentTimeMillis() - start);
    }

    @ShellMethod("delete")
    public String delete(@ShellOption(arity = 9999, defaultValue = "") String... cmd) {
        if (cmd.length == 0) {
            return "";
        }
        long start = System.currentTimeMillis();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int update = jdbcTemplate.update("delete " + String.join(" ", cmd));
        return String.format("影响了%s行,耗时:%sms", update, System.currentTimeMillis() - start);
    }

    @ShellMethod("create")
    public String create(@ShellOption(arity = 9999, defaultValue = "") String... cmd) {
        if (cmd.length == 0) {
            return "";
        }
        long start = System.currentTimeMillis();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int update = jdbcTemplate.update("create " + String.join(" ", cmd));
        return String.format("影响了%s行,耗时:%sms", update, System.currentTimeMillis() - start);
    }


    @ShellMethod("alter")
    public String alter(@ShellOption(arity = 9999, defaultValue = "") String... cmd) {
        if (cmd.length == 0) {
            return "";
        }
        long start = System.currentTimeMillis();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int update = jdbcTemplate.update("alter " + String.join(" ", cmd));
        return String.format("影响了%s行,耗时:%sms", update, System.currentTimeMillis() - start);
    }


    private String formatAsTable(List<Map<String, Object>> result) {
        if (result.isEmpty()) {
            return "No results found.";
        }

        StringBuilder sb = new StringBuilder();
        // 获取列名
        Map<String, Object> firstRow = result.getFirst();
        String[] columns = firstRow.keySet().toArray(new String[0]);

        // 计算每列的宽度
        int[] columnWidths = new int[columns.length];
        for (int i = 0; i < columns.length; i++) {
            columnWidths[i] = columns[i].length();
        }
        for (Map<String, Object> row : result) {
            for (int i = 0; i < columns.length; i++) {
                Object value = row.get(columns[i]);
                columnWidths[i] = Math.max(columnWidths[i], value != null ? value.toString().length() : 4); // 4 is the length of the word "null"
            }
        }

        // 打印表头
        for (int i = 0; i < columns.length; i++) {
            sb.append(String.format("%-" + columnWidths[i] + "s ", columns[i]));
        }
        sb.append("\n");

        // 打印分隔线
        for (int columnWidth : columnWidths) {
            sb.append("-".repeat(columnWidth)).append(" ");
        }
        sb.append("\n");

        // 打印数据行
        for (Map<String, Object> row : result) {
            for (int i = 0; i < columns.length; i++) {
                Object value = row.get(columns[i]);
                sb.append(String.format("%-" + columnWidths[i] + "s ", value != null ? value.toString() : "null"));
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
