package com.udea.parcial.service;

import com.udea.parcial.dto.InventoryRequest;
import com.udea.parcial.dto.InventoryResponse;
import com.udea.parcial.entity.Almacen;
import com.udea.parcial.entity.Inventory;
import com.udea.parcial.entity.Product;
import com.udea.parcial.repository.AlmacenRepository;
import com.udea.parcial.repository.InventoryRepository;
import com.udea.parcial.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;
    private final AlmacenRepository almacenRepo;
    private final InventoryMapper mapper;

    public InventoryServiceImpl(InventoryRepository inventoryRepo,
                                ProductRepository productRepo,
                                AlmacenRepository almacenRepo,
                                InventoryMapper mapper) {
        this.inventoryRepo = inventoryRepo;
        this.productRepo = productRepo;
        this.almacenRepo = almacenRepo;
        this.mapper = mapper;
    }

    @Override
    public List<InventoryResponse> getInventoryByAlmacen(Long almacenId) {
        return inventoryRepo.findByAlmacenId(almacenId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryResponse createInventory(InventoryRequest request) {
        Almacen almacen = almacenRepo.findById(request.getAlmacenId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El almacén no existe"));

        Product product = buildProduct(request);
        productRepo.save(product);

        Inventory inv = new Inventory(almacen, product, request.getStock());
        inventoryRepo.save(inv);

        return mapper.toResponse(inv);
    }

    private Product buildProduct(InventoryRequest request) {
        Product product = new Product();
        product.setName(request.getProductName());
        product.setDescription(request.getProductDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        return product;
    }
}
