package com.breweries.breweries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum State {
    AL("ALABAMA"),
    MT("MONTANA"),
    AK("ALASKA"),
    NE("NEBRASKA"),
    AZ("ARIZONA"),
    NV("NEVADA"),
    AR("ARKANSAS"),
    NH("NEWHAMPSHIRE"),
    CA("CALIFORNIA"),
    NJ("NEWJERSEY"),
    CO("COLORADO"),
    NM("NEWMEXICO"),
    CT("CONNECTICUT"),
    NY("NEWYORK"),
    DE("DELAWARE"),
    NC("NORTHCAROLINA"),
    FL("FLORIDA"),
    ND("NORTHDAKOTA"),
    GA("GEORGIA"),
    OH("OHIO"),
    HI("HAWAII"),
    OK("OKLAHOMA"),
    ID("IDAHO"),
    OR("OREGON"),
    IL("ILLINOIS"),
    PA("PENNSYLVANIA"),
    IN("INDIANA"),
    RI("RHODEISLAND"),
    IA("IOWA"),
    SC("SOUTHCAROLINA"),
    KS("KANSAS"),
    SD("SOUTHDAKOTA"),
    KY("KENTUCKY"),
    TN("TENNESSEE"),
    LA("LOUISIANA"),
    TX("TEXAS"),
    ME("MAINE"),
    UT("UTAH"),
    MD("MARYLAND"),
    VT("VERMONT"),
    MA("MASSACHUSETTS"),
    VA("VIRGINIA"),
    MI("MICHIGAN"),
    WA("WASHINGTON"),
    MN("MINNESOTA"),
    WV("WESTVIRGINIA"),
    MS("MISSISSIPPI"),
    WI("WISCONSIN"),
    MO("MISSOURI"),
    WY("WYOMING");
    private final String state;

    State(String state) {
        this.state = state;
    }

    public String getFullName() {
        return this.state;
    }

    public final static List<String> getAllStatesShortcutAsString(){
        return Arrays.stream(State.class.getEnumConstants()).map(State::name).collect(Collectors.toList());
    }

    public final static List<String> getAllStatesNameAsString(){
        return Arrays.stream(State.class.getEnumConstants()).map(State::getFullName).collect(Collectors.toList());
    }

    public final static State findStateEnumByShortcut(String stateShortcut){
        for(State state : State.class.getEnumConstants()){
            if(state.name().equals(stateShortcut.toUpperCase()))
                return state;
            }
        return null;
    }

    public final static State findStateEnumByName(String stateName){
        for(State state : State.class.getEnumConstants()){
            if(state.getFullName().equals(stateName.toUpperCase())){
                return state;
            }
        }
        return null;
    }
}