package com.test.automation.data;

/**
 * Produtos do catálogo do Sauce Demo: fonte única dos dados de cada produto
 * (nome, id, preço e descrição).
 *
 * Nas features, o produto é citado pela chave da constante (BACKPACK, BIKE_LIGHT...)
 * e o Cucumber converte para Product (ver ParameterTypes). Se o site mudar algum dado,
 * só este arquivo muda.
 */
public enum Product {

    BACKPACK("Sauce Labs Backpack", 4, 29.99,
            "carry.allTheThings() with the sleek, streamlined Sly Pack that melds uncompromising style with unequaled laptop and tablet protection."),
    BIKE_LIGHT("Sauce Labs Bike Light", 0, 9.99,
            "A red light isn't the desired state in testing but it sure helps when riding your bike at night. Water-resistant with 3 lighting modes, 1 AAA battery included."),
    BOLT_T_SHIRT("Sauce Labs Bolt T-Shirt", 1, 15.99,
            "Get your testing superhero on with the Sauce Labs bolt T-shirt. From American Apparel, 100% ringspun combed cotton, heather gray with red bolt."),
    FLEECE_JACKET("Sauce Labs Fleece Jacket", 5, 49.99,
            "It's not every day that you come across a midweight quarter-zip fleece jacket capable of handling everything from a relaxing day outdoors to a busy day at the office."),
    ONESIE("Sauce Labs Onesie", 2, 7.99,
            "Rib snap infant onesie for the junior automation engineer in development. Reinforced 3-snap bottom closure, two-needle hemmed sleeved and bottom won't unravel."),
    RED_T_SHIRT("Test.allTheThings() T-Shirt (Red)", 3, 15.99,
            "This classic Sauce Labs t-shirt is perfect to wear when cozying up to your keyboard to automate a few tests. Super-soft and comfy ringspun combed cotton.");

    private final String name;
    private final int id;
    private final double price;
    private final String description;

    Product(String name, int id, double price, String description) {
        this.name = name;
        this.id = id;
        this.price = price;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    /** Id que aparece na URL da página de detalhes (inventory-item.html?id=4). */
    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    /** Descrição exibida na página de detalhes. */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}