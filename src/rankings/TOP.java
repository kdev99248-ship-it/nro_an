/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rankings;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TOP {

    private String name;
    private byte gender;
    private short head;
    private short body;
    private short leg;
    private long power;
    private long ki;
    private long hp;
    private long sd;
    private byte nv;
    private byte subnv;
    private int sk;
    private long lasttime;
    private long time;
    private int level;
    private int cash;
    private int hlw;
    private int thoivang;
}
