package site.praytogether.pray_together;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LegalDocController {

  @GetMapping("/legal-document")
  public String getLegalDocuments(){
    return "legal-document";
  }
}
