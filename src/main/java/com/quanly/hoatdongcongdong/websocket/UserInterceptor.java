package com.quanly.hoatdongcongdong.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

//public class UserInterceptor implements ChannelInterceptor {
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
//
//            if (raw instanceof Map) {
//                Object name = ((Map) raw).get("username");
//                Object token = ((Map) raw).get("Authorization");
//                if (name instanceof ArrayList) {
//                    accessor.setUser(new User(((ArrayList<String>) name).get(0).toString()));
//                }
//            }
//        }
//        return message;
//    }
//
//}
public class UserInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);

            if (raw instanceof Map) {
                Object name = ((Map) raw).get("username");
                Object token = ((Map) raw).get("Authorization");

                if (name instanceof ArrayList ) {
                    String jwtToken = token.toString().substring(7);
                    if (JwtUtils.validateJwtToken(jwtToken)) {
                        accessor.setUser(new User(((ArrayList<String>) name).get(0))); // Xác thực thành công
                    } else {
                        throw new SecurityException("JWT Token is invalid"); // Xác thực thất bại
                    }
                }
            }
        }
        return message;
    }
}
