package org.example.backend_fivegivechill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//là một annotation trong Spring Framework, được sử dụng để kích hoạt tính năng lập
//lịch(scheduling) trong ứng dụng của bạn. Khi bạn thêm annotation
//này,
//Spring sẽ
//tự động
//tìm và
//thực thi
//các phương
//thức được
//đánh dấu
//bằng @Scheduled            quá rõ ràng luôn

public class BackendFiveGiveChillApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendFiveGiveChillApplication.class, args);
    }

}
