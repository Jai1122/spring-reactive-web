package com.jaiplays.services.otp.repository;

import com.jaiplays.services.otp.entity.Application;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ApplicationRepository extends ReactiveCrudRepository<Application,String> {
}
