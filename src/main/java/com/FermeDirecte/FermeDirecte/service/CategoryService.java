// src/main/java/com/FermeDirecte/FermeDirecte/service/CategoryService.java
package com.FermeDirecte.FermeDirecte.service;

import com.FermeDirecte.FermeDirecte.dto.category.*;
import com.FermeDirecte.FermeDirecte.entity.Category;
import com.FermeDirecte.FermeDirecte.exception.BusinessException;
import com.FermeDirecte.FermeDirecte.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getArbre() {
        return categoryRepository.findByParentIsNull()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse creer(CategoryRequest request) {
        Category category = Category.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .build();

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("Catégorie parente introuvable", HttpStatus.NOT_FOUND));
            category.setParent(parent);
        }

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse modifier(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Catégorie introuvable", HttpStatus.NOT_FOUND));

        category.setNom(request.getNom());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("Catégorie parente introuvable", HttpStatus.NOT_FOUND));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void supprimer(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new BusinessException("Catégorie introuvable", HttpStatus.NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponse toResponse(Category c) {
        List<CategoryResponse> sousCategories = c.getSousCategories().stream()
                .map(this::toResponse).collect(Collectors.toList());

        return CategoryResponse.builder()
                .id(c.getId())
                .nom(c.getNom())
                .description(c.getDescription())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .sousCategories(sousCategories)
                .build();
    }
}
