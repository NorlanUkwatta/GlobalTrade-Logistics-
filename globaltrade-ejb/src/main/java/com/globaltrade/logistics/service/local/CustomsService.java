package com.globaltrade.logistics.service.local;

import com.globaltrade.logistics.entity.CustomsDeclaration;
import java.util.List;

public interface CustomsService {
    CustomsDeclaration submitDeclaration(Long shipmentId, Double dutyAmount);
    CustomsDeclaration updateStatus(Long declarationId, CustomsDeclaration.Status status, String remarks);
    List<CustomsDeclaration> findPending();
    List<CustomsDeclaration> findAll();
}