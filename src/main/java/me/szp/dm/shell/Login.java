package me.szp.dm.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * @author szp
 * @since 2024/6/13 14:33
 */
@ShellComponent
public class Login {

    private String username;

    private String password;


    @ShellMethod("用户名")
    public void username(String username) {
        this.username = username;
    }

    @ShellMethod("密码")
    public void password(String password) {
        this.password = password;
    }


}
