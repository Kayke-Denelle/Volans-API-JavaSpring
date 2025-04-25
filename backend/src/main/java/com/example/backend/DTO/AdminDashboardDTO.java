package com.example.backend.DTO;

public class AdminDashboardDTO {
    private long totalUsuarios;
    private long totalBaralhos;
    private long totalFlashcards;
    public long getTotalUsuarios() {
        return totalUsuarios;
    }
    public void setTotalUsuarios(long totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }
    public long getTotalBaralhos() {
        return totalBaralhos;
    }
    public void setTotalBaralhos(long totalBaralhos) {
        this.totalBaralhos = totalBaralhos;
    }
    public long getTotalFlashcards() {
        return totalFlashcards;
    }
    public void setTotalFlashcards(long totalFlashcards) {
        this.totalFlashcards = totalFlashcards;
    }
}
