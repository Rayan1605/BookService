package Backend.libaryproject.Interface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name = "Payment-service",url = "https://localhost:8082")
public interface Payment {

        @PostMapping("/api/payment/save")
        Backend.libaryproject.Entity.Payment save(@RequestBody Backend.libaryproject.Entity.Payment payment);

        @PostMapping("/api/payment/findByUserEmail")
        Backend.libaryproject.Entity.Payment findByUserEmail(@RequestBody String userEmail);
}
