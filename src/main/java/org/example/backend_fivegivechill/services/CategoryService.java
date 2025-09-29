package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.CategoryBean;
import org.example.backend_fivegivechill.entity.CategoryEntity;
import org.example.backend_fivegivechill.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Page<CategoryEntity> getAllCategoryByStatus(int status, String search, Pageable pageable) {
        return categoryRepository.findAllByStatus(status, "%"+search+"%", pageable);
    }

    public List<CategoryEntity> getAllCategory() {
        return categoryRepository.getAll();
    }

    public CategoryEntity getCategoryById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public CategoryEntity addCategory(CategoryBean categoryBean) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryBean.getName());
        categoryEntity.setStatus(categoryBean.getStatus());
        return categoryRepository.save(categoryEntity);
    }

    public CategoryEntity existCategoryAdd(CategoryBean categoryBean) {
        CategoryEntity existCategory = categoryRepository.existByName(categoryBean.getName());
        if (existCategory != null) {
            return existCategory;
        }
        return null;
    }

    public CategoryEntity updateCategory(int id, CategoryBean categoryBean) {
        CategoryEntity exist = categoryRepository.findById(id).orElse(null);
        if (exist != null) {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setId(id);
            categoryEntity.setName(categoryBean.getName());
            categoryEntity.setStatus(categoryBean.getStatus());
            return categoryRepository.save(categoryEntity);
        } else {
            return null;
        }
    }

    public CategoryEntity existCategoryUpdate(int id, CategoryBean categoryBean) {
        CategoryEntity existCategory = categoryRepository.existByNameAndId(categoryBean.getName(), id);
        if (existCategory != null) {
            return existCategory;
        }
        return null;
    }
}
