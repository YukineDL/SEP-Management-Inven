package com.inventorymanagement.utils;

import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
@Configuration
@Slf4j
public class SecurityUtils {
    @Value("${jwt.signerKey}")
    private String signerKey;

    public String generate(Employee employee) throws InventoryException {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(employee.getUsername())
                .subject(employee.getUsername())
                .expirationTime(new Date(
                        Instant.now().plus(4, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("role_code", employee.getRoleCode())
                .claim("employee_code", employee.getCode())
                .claim("employee_id",employee.getId())
                .build();
        Payload payload = new Payload(claims.toJSONObject());
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWSObject token = new JWSObject(header,payload);
        try {
            token.sign(new MACSigner(signerKey.getBytes()));
        } catch (JOSEException e){
            throw new InventoryException(
                    ExceptionMessage.INTERNAL_SERVER_ERROR,
                    ExceptionMessage.messages.get(ExceptionMessage.INTERNAL_SERVER_ERROR)
            );
        }
        return token.serialize();
    }
    public String decode(String authHeader){
         return authHeader.substring(7);
    }

    public String getCurrentUser() {
        try {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return (String) jwt.getClaims().get("issuer");
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }
}
