package Questions;

import java.io.Serializable;

public class QuestionsAndAnswers implements Serializable {
    private String Question;
    private String CorrectAnswer;
    private String answer2;
    private String answer3;
    private String answer4;
    public QuestionsAndAnswers(String question, String correctAnswer, String answer2, String answer3, String answer4) {
        Question = question;
        CorrectAnswer = correctAnswer;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }


    public String getQuestion() {
        return Question;
    }

    public String getCorrectAnswer() {
        return CorrectAnswer;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    @Override
    public String toString() {
        return
                "Question: " + Question +
                ", " + CorrectAnswer +
                ", " + answer2 +
                ", " + answer3 +
                ", " + answer4;
    }
}
