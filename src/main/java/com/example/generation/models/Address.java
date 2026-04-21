package com.example.generation.models;

import jakarta.persistence.*;

    @Entity
    @Table(name = "address")
    public class Address {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "address_line", nullable = false)
        private String addressLine;

        @Column(name = "postal_code", nullable = false)
        private String postalCode;

        @Column(nullable = false)
        private String city;

        @Column(nullable = false)
        private String country;

        public Address() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getAddressLine() { return addressLine; }
        public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }


