package com.chisimdi.order.service.utils;

import com.chisimdi.events.Location;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MakeAnOrderUtil {
    @Positive
    private int cartId;
    @Positive
    private int userId;
    @Positive
    private int accountId;
    @NotNull
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

}
