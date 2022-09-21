package com.hr.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author swq
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class HealthApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");

        SpringApplication.run(HealthApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  health启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "                     ,--,                                                       \n" +
                "  .--.--.          ,'_ /|                                 .--.--.    .--.--.               \n" +
                " /  /    '    .--. |  | :    ,---.     ,---.     ,---.   /  /    '  /  /    '              \n" +
                "|  :  /`./  ,'_ /| :  . |   /     \\   /     \\   /     \\ |  :  /`./ |  :  /`./           \n" +
                "|  :  ;_    |  ' | |  . .  /    / '  /    / '  /    /  ||  :  ;_   |  :  ;_                \n" +
                " \\  \\    `. |  | ' |  | | .    ' /  .    ' /  .    ' / | \\  \\    `. \\  \\    `.       \n" +
                "  `----.   \\:  | : ;  ; | '   ; :__ '   ; :__ '   ;   /|  `----.   \\ `----.   \\         \n" +
                " /  /`--'  /'  :  `--'   \\'   | '.'|'   | '.'|'   |  / | /  /`--'  //  /`--'  /           \n" +
                "'--'.     / :  ,      .-./|   :    :|   :    :|   :    |'--'.     /'--'.     /             \n" +
                "  `--'---'   `--`----'     \\   \\  /  \\   \\  /  \\   \\  /   `--'---'   `--'---'        \n" +
                "                            `----'    `----'    `----'                                     \n");
    }
}
