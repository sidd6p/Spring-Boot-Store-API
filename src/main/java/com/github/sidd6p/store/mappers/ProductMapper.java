package com.github.sidd6p.store.mappers;

import com.github.sidd6p.store.dtos.ProductDto;
import com.github.sidd6p.store.dtos.RegisterProductRequest;
import com.github.sidd6p.store.entities.Category;
import com.github.sidd6p.store.entities.Product;
import com.github.sidd6p.store.repositories.CategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Autowired
    protected CategoryRepository categoryRepository;

    @Mapping(source = "category.name", target = "categoryName")
    public abstract ProductDto toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "category_id", target = "category", qualifiedByName = "mapCategory")
    public abstract Product toEntity(RegisterProductRequest registerProductRequest);

    /*
     * Flow explanation for mapCategory:
     *
     * The @Mapping annotation uses 'qualifiedByName = "mapCategory"' to specify that the 'category_id' field from RegisterProductRequest
     * should be mapped to the 'category' field in Product using the custom mapCategory method below.
     *
     * When MapStruct generates the implementation, it will call mapCategory(category_id) to fetch the Category entity from the database
     * using CategoryRepository. This enables mapping an ID to a full entity, which is not possible with default MapStruct logic.
     *
     * Flow:
     *   [RegisterProductRequest.category_id]
     *            |
     *            v
     *   ProductMapper.mapCategory(category_id)
     *            |
     *            v
     *   [Category entity fetched from DB]
     *            |
     *            v
     *   [Product.category]
     */
    /*
     * @Named annotation explanation:
     *
     * The @Named annotation is used by MapStruct to identify custom mapping methods.
     * By specifying @Named("mapCategory"), this method can be referenced in @Mapping annotations using 'qualifiedByName = "mapCategory"'.
     * This enables MapStruct to use this specific method for mapping when converting fields, allowing for advanced or custom logic.
     */
    @Named("mapCategory")
    protected Category mapCategory(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId).orElse(null);
    }
}

/*
 * Why is ProductMapper an abstract class and not an interface?
 *
 * ProductMapper uses custom mapping logic (the mapCategory method) that cannot be handled by MapStruct's default interface-based generation.
 * By making it an abstract class, you can provide method implementations (like mapCategory) that MapStruct will use in the generated code.
 *
 * If ProductMapper were an interface, you could not inject dependencies (like CategoryRepository) or provide custom mapping methods directly.
 * Abstract classes allow for dependency injection and custom logic, which is necessary for advanced mapping scenarios.
 */
