package com.graphy.lms.entity;

public enum DeliveryStatus {
    PENDING,    // Request raised, but not yet sent
    IN_TRANSIT, // Item is on the way/being moved
    DELIVERED,  // Successfully received by student/staff
    RETURNED,   // Item returned to inventory
    CANCELLED   // Assignment cancelled
}
