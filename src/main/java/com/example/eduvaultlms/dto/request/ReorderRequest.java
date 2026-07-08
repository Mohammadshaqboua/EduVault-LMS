package com.example.eduvaultlms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReorderRequest(
        @NotNull(message = "New order index is required")
        @Min(value = 0, message = "Order index must be positive")
        Integer newOrderIndex
) {}
