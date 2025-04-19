package com.techblog.controller;

import com.techblog.dto.Result;
import com.techblog.entity.DiscountCode;
import com.techblog.service.DiscountCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discount-codes")
public class DiscountCodeController {

    @Autowired
    private DiscountCodeService discountCodeService;

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return discountCodeService.getById(id);
    }

    @GetMapping("/code/{code}")
    public Result getByCode(@PathVariable String code) {
        return discountCodeService.getByCode(code);
    }

    @GetMapping
    public Result listAll() {
        return discountCodeService.listAll();
    }

    @PostMapping
    public Result create(@RequestBody DiscountCode discountCode) {
        return discountCodeService.create(discountCode);
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody DiscountCode discountCode) {
        discountCode.setId(id);
        return discountCodeService.update(discountCode);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return discountCodeService.deleteById(id);
    }

    @PutMapping("/{id}/status")
    public Result updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        return discountCodeService.updateStatus(id, status);
    }

    @PostMapping("/{id}/claim")
    public Result claimDiscountCode(@PathVariable Integer id, @RequestParam Integer userId) {
        return discountCodeService.claimDiscountCode(userId, id);
    }

    @PostMapping("/{id}/use")
    public Result useDiscountCode(@PathVariable Integer id, @RequestParam Integer userId) {
        return discountCodeService.useDiscountCode(userId, id);
    }

    @GetMapping("/{id}/check")
    public Result checkDiscountCode(@PathVariable Integer id, @RequestParam Integer userId) {
        return discountCodeService.hasClaimedDiscountCode(userId, id);
    }
}