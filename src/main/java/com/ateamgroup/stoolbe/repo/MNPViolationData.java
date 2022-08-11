package com.ateamgroup.stoolbe.repo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MNPViolationData")
@Getter
@Setter
@NoArgsConstructor

public class MNPViolationData {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator ="violation_sequence")
    private long id ;

    @Column(name = "port_id")
    private String port_id;

    @Column(name = "Type")
    private String Type ;

    @Column(name = "Violation_Date")
    private Date Violation_Date ;

    @Column(name = "Reason")
    private String Reason ;

    @Column(name = "Added_by")
    private String Added_by ;

    @Column(name = "Added_Date")
    private String Added_Date ;

    @Column(name = "Comment")
    private String Comment ;

    public MNPViolationData(String port_id, String type, Date violation_Date, String reason, String added_by, String added_Date, String comment) {
        this.port_id = port_id;
        Type = type;
        Violation_Date = violation_Date;
        Reason = reason;
        Added_by = added_by;
        Added_Date = added_Date;
        Comment = comment;
    }
}
