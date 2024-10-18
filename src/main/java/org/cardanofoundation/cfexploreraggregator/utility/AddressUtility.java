package org.cardanofoundation.cfexploreraggregator.utility;

public class AddressUtility {

    private AddressUtility() {
    }

    public static boolean isShelleyAddress(String address) {
        return address.startsWith("addr1");
    }
}
