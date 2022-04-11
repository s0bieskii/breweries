package com.breweries.breweries;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private String fullName;

    State(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public static List<String> getAllStatesShortcutAsString() {
        return Arrays.stream(values())
                .map(Enum::name)
                .toList();
    }

    public static List<String> getAllStatesNameAsString() {
        return Arrays.stream(values())
                .map(State::getFullName)
                .toList();
    }

    public static State findStateEnumByShortcut(String stateShortcut) {
        try {
            return State.valueOf(stateShortcut);
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    public static State findStateEnumByName(String stateName) {
        return Arrays.stream(values())
                .filter(value -> Objects.equals(value.getFullName(), stateName))
                .findFirst()
                .orElse(null);
    }
}