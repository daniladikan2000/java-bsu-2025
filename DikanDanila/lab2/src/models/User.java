package models;

import java.util.List;
import java.util.UUID;

public class User {
    private UUID id;
    private String nickname;
    private List<String> accountIds;

    public User(String nickname, List<String> accountIds) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("Никнейм не может быть пустым.");
        }
        this.id = UUID.randomUUID();
        this.nickname = nickname;
        this.accountIds = accountIds;
    }

    // Конструктор для восстановления из БД (где ID уже есть)
    public User(UUID id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public List<String> getAccountIds() { return accountIds; }
    public void setAccountIds(List<String> accountIds) { this.accountIds = accountIds; }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", accountIds=" + accountIds +
                '}';
    }
}