package com.example.ifme;

// 카드 한 장에 들어갈 정보 (세계 생성할 때 입력한 "예상")
public class NodeCard {
    int branchNumber;   // 제 N 분기점
    String title;       // 선택지 제목 (예: 취업하기)
    String[] values;    // 중요하게 생각하는 가치 (최대 3개)
    String expect;      // 기대하는 점
    String worry;       // 걱정하는 점
    String giveUp;      // 포기해야할 것
    int satisfaction;   // 예상 만족도 (0~10)

    public NodeCard(int branchNumber, String title, String[] values, String expect,
                    String worry, String giveUp, int satisfaction) {
        this.branchNumber = branchNumber;
        this.title = title;
        this.values = values;
        this.expect = expect;
        this.worry = worry;
        this.giveUp = giveUp;
        this.satisfaction = satisfaction;
    }
}