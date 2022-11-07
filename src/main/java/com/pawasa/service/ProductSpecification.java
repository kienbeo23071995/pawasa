package com.pawasa.service;

import com.pawasa.model.Category;
import com.pawasa.model.Product;
import com.pawasa.model.Product_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public final class ProductSpecification {
    public static Specification<Product> startWith(String word) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Product_.PRODUCT_NAME), word + " %"));
    }
    public static Specification<Product> endWith(String word) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Product_.PRODUCT_NAME), "% " + word));
    }
    public static Specification<Product> hasPublisher(String name){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Product_.PUBLISHER), "%"+name+"%"));
    }
    public static Specification<Product> hasAuthor(String name){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Product_.AUTHOR), "%"+name+"%"));
    }
    public static Specification<Product> hasProductId(List<Long> idLst) {
        return ((root, query, criteriaBuilder) -> root.get(Product_.ID).in(idLst));
    }

    public static Specification<Product> hasId(Category category) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Product_.CATEGORY), category);
    }

    /**
     * @param bookLayoutID ID = 1 - Bìa Mềm
     *                     ID = 2 - Bìa Cứng
     */
    public static Specification<Product> hasBookLayout(int bookLayoutID) {
        if (bookLayoutID == 2) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Product_.BOOK_LAYOUT), "Bìa Mềm"));
        }
        if (bookLayoutID == 1) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Product_.BOOK_LAYOUT), "Bìa Cứng"));
        }
        if (bookLayoutID == 3) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(Product_.BOOK_LAYOUT), "Bìa Mềm"));
        }
        return null;
    }

    /**
     * @param languageID ID = 1 - Tiếng Việt
     *                   ID = 2 - Tiếng Anh
     *                   ID = 3 - Khác
     * @return
     */
    public static Specification<Product> hasLanguage(int languageID) {
        String language = (languageID == 1) ? "Tiếng Việt" : ((languageID == 2) ? "Tiếng Anh" : "Khác");
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Product_.LANGUAGE), language);
    }

    /**
     * @return
     */
    public static Specification<Product> priceInRange(int priceID) {
        if (priceID == 1) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Product_.PRICE), 0, 150000));
        }
        if (priceID == 2) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Product_.PRICE), 150000, 300000));
        }
        if (priceID == 3) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Product_.PRICE), 300000, 500000));
        }
        if (priceID == 4) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Product_.PRICE), 500000, 700000));
        }
        if (priceID == 5) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Product_.PRICE), 700000, Integer.MAX_VALUE));
        }
        return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Product_.PRICE), 0, Integer.MAX_VALUE));
    }

    public static Specification<Product> qtyGreaterThan0(int value) {
        if(value == 1){
            return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(Product_.QUANTITY), 0));
        }else{
            return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(Product_.QUANTITY), 0));
        }
    }
}
