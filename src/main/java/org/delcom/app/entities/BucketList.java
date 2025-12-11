package org.delcom.app.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bucket_list")
public class BucketList {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String destination;

    @Column(length = 500)
    private String notes;
    
    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "is_achieved")
    private boolean isAchieved = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // Constructor Kosong (Wajib untuk JPA)
    public BucketList() {}
    
    // Constructor Lengkap
    public BucketList(User user, String destination, String notes, LocalDate targetDate) {
        this.user = user;
        this.destination = destination;
        this.notes = notes;
        this.targetDate = targetDate;
    }

    // --- Getters & Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    
    public boolean isAchieved() { return isAchieved; }
    public void setAchieved(boolean achieved) { isAchieved = achieved; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}