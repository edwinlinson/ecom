//package com.example.demo.Handlers;
//
//import com.example.demo.Service.TwilioService;
//import com.example.demo.dto.TwilioDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Mono;
//
//@Component
//public class TwilioOtpHAndler {
//    @Autowired
//    TwilioService service;
//    public Mono<ServerResponse> sendOTP(ServerRequest serverRequest){
//
//        return serverRequest.bodyToMono(TwilioDto.class)
//                .flatMap(dto -> service.sendOtpReg(dto))
//                .flatMap(dto -> ServerResponse.status(HttpStatus.OK)
//                        .body(BodyInserters.fromValue(dto)));
//    }
//    public Mono<ServerResponse> validateOTP(ServerRequest serverRequest){
//        return serverRequest.bodyToMono(TwilioDto.class)
//                .flatMap(dto -> service.validateOtp(dto.getOtp(),dto.getEmail()))
//                .flatMap(dto -> ServerResponse.status(HttpStatus.OK)
//                        .bodyValue(dto));
//    }
//}
