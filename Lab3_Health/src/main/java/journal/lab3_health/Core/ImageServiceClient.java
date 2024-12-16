package journal.lab3_health.Core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

//@FeignClient(name = "image-service", url = "http://localhost:3001")
@FeignClient(name = "image-service", url = "https://journal-app-image.app.cloud.cbh.kth.se")
public interface ImageServiceClient {
    @PostMapping(value = "/create-binary", consumes = "multipart/form-data")
    String createBinary(@RequestPart("image") MultipartFile image);
}
