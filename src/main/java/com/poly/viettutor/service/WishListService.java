package com.poly.viettutor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.viettutor.repository.WishListRepository;
import com.poly.viettutor.model.User;
import com.poly.viettutor.model.Wishlist;
import java.util.List;

@Service
public class WishListService {

    @Autowired
    private WishListRepository wishListRepository;

    public List<Wishlist> getWishlistByUser(User user) {
        return wishListRepository.findByUser(user);
    }
}
