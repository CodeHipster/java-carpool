package thijs.oostdam.carpool.email;

public class Email {
    private String toAddress;
    private String subject;
    private String body;

    /**
     *
     * @param toAddress in the form of "Mail <mail@test.com>"
     * @param subject
     * @param body
     */
    public Email(String toAddress, String subject, String body){
        this.body = body;
        this.subject = subject;
        this.toAddress = toAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
