package Sport;

import java.io.Serializable;

public class SportQ implements Serializable {
    private String Question;
    private String Correctanswer;
    private String answer2;
    private String answer3;
    private String answer4;

    public SportQ(String question, String correctanswer, String answer2, String answer3, String answer4) {
        Question = question;
        Correctanswer = correctanswer;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getCorrectanswer() {
        return Correctanswer;
    }

    public void setCorrectanswer(String correctanswer) {
        Correctanswer = correctanswer;
    }

    @Override
    public String toString() {
        return  Question +
                ", Correctanswer " + Correctanswer +
                ", " + answer2 +
                ", " + answer3 +
                ", " + answer4;
    }
}
