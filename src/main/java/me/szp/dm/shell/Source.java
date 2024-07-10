package me.szp.dm.shell;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.shell.standard.FileValueProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author szp
 * @since 2024/7/10 11:00
 */
@ShellComponent
@RequiredArgsConstructor
public class Source {

    private final DataSource dataSource;

    @ShellMethod("source")
    public void source(@ShellOption(valueProvider = FileValueProvider.class) String filePath) {
        File file = new File(filePath);
        File path = file.getParentFile();
        String name = file.getName();

        if (!path.exists()) {
            System.err.println("dir does not exists: " + path);
            return;
        }

        List<Path> matchedFiles = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path.getPath()), name)) {
            for (Path entry : stream) {
                matchedFiles.add(entry);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        matchedFiles.forEach(f -> {

            System.out.println(">>>> start execute: " + f.getFileName());

            try (Connection connection = DataSourceUtils.getConnection(dataSource);

                 Reader reader = new FileReader(f.toFile())) {
                //创建脚本执行器
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                //设置执行器日志输出
                scriptRunner.setLogWriter(new PrintWriter(System.out));
                //设置执行器错误日志输出
                scriptRunner.setErrorLogWriter(new PrintWriter(System.err));
                //设置读取文件格式
                Resources.setCharset(StandardCharsets.UTF_8);

                scriptRunner.runScript(reader);

            } catch (IOException | SQLException e) {
                e.printStackTrace(System.err);
            }
        });


    }


}
