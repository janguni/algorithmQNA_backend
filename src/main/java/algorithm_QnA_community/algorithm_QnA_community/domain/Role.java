package algorithm_QnA_community.algorithm_QnA_community.domain;


public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    String role;

    Role(String role){
        this.role = role;
    }

    public String value(){
        return role;
    }
}