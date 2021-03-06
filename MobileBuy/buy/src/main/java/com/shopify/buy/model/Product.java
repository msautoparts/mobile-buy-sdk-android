/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Shopify Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.shopify.buy.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.shopify.buy.utils.DateUtility;
import com.shopify.buy.utils.DateUtility.DateDeserializer;

import java.util.ArrayList;
import java.util.Date;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@code Product} is an individual item for sale in a Shopify store.
 */
public class Product extends ShopifyObject {

    @SerializedName("product_id")
    private String productId;

    @SerializedName("channel_id")
    private String channelId;

    private String title;

    private String handle;

    @SerializedName("body_html")
    private String bodyHtml;

    @SerializedName("published_at")
    private Date publishedAtDate;

    @SerializedName("created_at")
    private Date createdAtDate;

    @SerializedName("updated_at")
    private Date updatedAtDate;

    private String vendor;

    @SerializedName("product_type")
    private String productType;

    private List<ProductVariant> variants;

    private List<Image> images;

    private List<Option> options;

    private String tags;

    private Set<String> tagSet;

    private boolean available;

    private boolean published;

    /**
     * @return {@code true} if this product has been published on the store, {@code false} otherwise.
     */
    public boolean isPublished() {
        return published;
    }

    /**
     * @return The unique identifier for this product.
     */
    public String getProductId() {
        return productId;
    }

    /**
     * @return The unique identifier of the Mobile App sales channel for this store.
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * @return The title of this product.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The handle of the product. Can be used to construct links to the web page for the product.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * @return The description of the product, complete with HTML formatting.
     */
    public String getBodyHtml() {
        return bodyHtml;
    }

    /**
     * Use {@link #getPublishedAtDate() getPublishedAtDate()}.
     */
    @Deprecated
    public String getPublishedAt() {
        return publishedAtDate == null ? null : DateUtility.createDefaultDateFormat().format(publishedAtDate);
    }

    /**
     * Use {@link #getCreatedAtDate() getCreatedAtDate()}.
     */
    @Deprecated
    public String getCreatedAt() {
        return createdAtDate == null ? null : DateUtility.createDefaultDateFormat().format(createdAtDate);
    }

    /**
     * Use {@link #getUpdatedAtDate() getUpdatedAtDate()}.
     */
    @Deprecated
    public String getUpdatedAt() {
        return updatedAtDate == null ? null : DateUtility.createDefaultDateFormat().format(updatedAtDate);
    }

    /**
     * @return The date this product was published.
     */
    public Date getPublishedAtDate() {
        return publishedAtDate;
    }

    /**
     * @return The date this product was created.
     */
    public Date getCreatedAtDate() {
        return createdAtDate;
    }

    /**
     * @return The date this product was last updated.
     */
    public Date getUpdatedAtDate() {
        return updatedAtDate;
    }

    /**
     * @return The name of the vendor of this product.
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @return The categorization that this product was tagged with, commonly used for filtering and searching.
     */
    public String getProductType() {
        return productType;
    }

    /**
     * @return A list of additional categorizations that a product can be tagged with, commonly used for filtering and searching. Each tag has a character limit of 255.
     */
    public Set<String> getTags() { return tagSet; }

    /**
     * @return A list {@link ProductVariant} objects, each one representing a different version of this product.
     */
    public List<ProductVariant> getVariants() {
        return variants;
    }

    /**
     * @return A list of {@link Image} objects, each one representing an image associated with this product.
     */
    public List<Image> getImages() {
        return images;
    }

    /**
     * @return {code true} if this product has at least one image, {@code false} otherwise.
     */
    public boolean hasImage() {
        return images != null && !images.isEmpty();
    }

    /**
     * @return A list of {@link Option} objects, which can be used to select a specific {@link ProductVariant}.
     */
    public List<Option> getOptions() {
        return options;
    }

    /**
     * @return {@code true} if this product is in stock and available for purchase, {@code false} otherwise.
     */
    public boolean isAvailable() { return available; }

    /**
     * For internal use only.
     */
    public boolean hasDefaultVariant() {
        return variants != null && variants.size() == 1 && variants.get(0).getTitle().equals("Default Title");
    }

    /**
     * Returns the {@code Image} for the {@code ProductVariant} with the given id
     * @param variant the {@link ProductVariant} to find the {@link Image}
     * @return the {@link Image} corresponding to the {@link ProductVariant} if one was found, otherwise the {@code Image} for the {@link Product}.  This may return null if no applicable images were found.
     */
    public Image getImage(ProductVariant variant) {
        if (variant == null) {
            throw new NullPointerException("variant cannot be null");
        }

        List<Image> images = getImages();

        if (images == null || images.size() < 1) {
            // we did not find any images
            return null;
        }

        for (Image image : images) {
            if (image.getVariantIds().contains(variant.getId())) {
                return image;
            }
        }

        // The variant did not have an image, use the default image in the Product
        return images.get(0);
    }

    /**
     * @param optionValues  A list of {@link OptionValue} objects that represent a specific variant selection.
     * @return  The {@link ProductVariant} that matches the given list of the OptionValues, or {@code null} if no such variant exists.
     */
    public ProductVariant getVariant(List<OptionValue> optionValues) {
        if (optionValues == null) {
            return null;
        }

        int numOptions = optionValues.size();
        for (ProductVariant variant : variants) {
            for (int i = 0; i < numOptions; i++) {
                if (!variant.getOptionValues().get(i).getValue().equals(optionValues.get(i).getValue())) {
                    break;
                } else if (i == numOptions - 1) {
                    return variant;
                }
            }
        }

        return null;
    }

    public static class ProductDeserializer implements JsonDeserializer<Product> {

        @Override
        public Product deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return fromJson(json.toString());
        }

    }

    /**
     * A product object created using the values in the JSON string.
     */
    public static Product fromJson(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        Product product = gson.fromJson(json, Product.class);

        List<ProductVariant> variants = product.getVariants();

        if (variants != null) {
            for (ProductVariant variant : variants) {
                variant.productId = Long.parseLong(product.productId);
                variant.productTitle = product.getTitle();
            }
        }

        // Create the tagSet.
        product.tagSet = new HashSet<>();

        // Populate the tagSet from the comma separated list.
        if (!TextUtils.isEmpty(product.tags)) {
            for (String tag : product.tags.split(",")) {
                String myTag = tag.trim();
                product.tagSet.add(myTag);
            }
        }

        return product;
    }

}
