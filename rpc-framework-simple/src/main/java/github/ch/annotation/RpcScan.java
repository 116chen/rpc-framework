package github.ch.annotation;


import github.ch.spring.RpcScannerRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import(RpcScannerRegister.class)
public @interface RpcScan {
    String[] basePackage();
}
