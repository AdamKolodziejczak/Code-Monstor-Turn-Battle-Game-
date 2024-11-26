package com.example.webbattle;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Skill {
    public enum SkillGroup {
        ATTACK,
        SUPPORT,
        HEAL,
        HYBRID,
    }

    @JsonProperty("name")
    public String name;
    @JsonProperty("type")
    public DmgType type;
    public double dmgDealt;
    public double dmgHealed;
    public double accuracy;
    public double atkBoost;
    public double defBoost;
    @JsonProperty("cost")
    public double staminaCost;
    public String description;

    public Skill(String name, DmgType type, SkillGroup group, double dmgDealt, double dmgHealed, double accuracy,
                 double atkBoost, double defBoost, double staminaCost, String description){
        this.name = name;
        this.type = type;
        this.dmgDealt = dmgDealt;
        this.dmgHealed = dmgHealed;
        this.accuracy = accuracy;
        this.atkBoost = atkBoost;
        this.defBoost = defBoost;
        this.staminaCost = staminaCost;

        this.description = generateDescription(description);
    }

    private String generateDescription(String desc) {
        return "cost: " + staminaCost + ", paradigm: " + type.name().toLowerCase() + ". " + desc;
    }

    public static Skill getSkill(String name) {
        for (Skill skill : skills) {
            if (name.equals(skill.name)) {
                return skill;
            }
        }
        throw new RuntimeException("Invalid skill name: " + name);
    }

    static List<Skill> skills = new ArrayList<>(){
        {
            add(new Skill("BasicAttack", DmgType.OBJECT_ORIENTED, SkillGroup.ATTACK, 55, 0, 45,
                    0, 0, 10, "Deals a small amount of damage. Atk: 55, Acc: 45"));
            add(new Skill("StrongAttack", DmgType.OBJECT_ORIENTED, SkillGroup.ATTACK, 75, 0, 35,
                    0, 0, 15, "Deals a large amount of damage. Atk: 75, Acc: 35"));
            add(new Skill("BasicHeal", DmgType.OBJECT_ORIENTED, SkillGroup.HEAL, -100, 50, 90,
                    0, 0, 25, "Heals you a small amount. Heal: 50"));
            add(new Skill("StrongHeal", DmgType.OBJECT_ORIENTED, SkillGroup.HEAL, -100, 75, 90,
                    0, 0, 35, "Heals you a large amount. Heal: 75"));
            add(new Skill("AtkBoostAttack", DmgType.PROCEDURAL, SkillGroup.HYBRID, 45, 0, 40,
                    10, 0, 25, "Boosts your attack and deals damage to the enemy. Atk: 45, Acc: 40, AtkBoost: 10"));
            add(new Skill("DefBoostAttack", DmgType.PROCEDURAL, SkillGroup.HYBRID, 45, 0, 40,
                    0, 10, 25, "Boosts your defence and deals damage to the enemy. Atk: 45, Acc: 40, DefBoost: 10"));
            add(new Skill("ReduceAttack", DmgType.PROCEDURAL, SkillGroup.ATTACK, 85, 0, 35,
                    -5, -5, 30, "Deals high damage to the enemy, but reduces your attack and defence. Atk: 85, Acc: 35, AtkBoost: -5, DefBoost: -5"));
            add(new Skill("SelfDmgAttack", DmgType.FUNCTIONAL, SkillGroup.HYBRID, 95, -20, 30,
                    0, 0, 30, "Deals massive damage to the enemy, but also damages you. Atk: 95, Heal: -20, Acc: 30"));
            add(new Skill("BoostAttack", DmgType.FUNCTIONAL, SkillGroup.SUPPORT, 45, 0, 40,
                    10, 10, 40, "Damages the enemy and slightly raises both attack and defence. Atk: 45, Acc: 40, AtkBoost: 10, DefBoost: 10"));
            add(new Skill("AtkBoost", DmgType.FUNCTIONAL, SkillGroup.SUPPORT, -100, 0, 60,
                    20, 0, 30, "Boosts your damage on later attacks. Acc: 60, AtkBoost: 20"));
            add(new Skill("HealAttack", DmgType.UNSTRUCTURED, SkillGroup.HYBRID, 55, 40, 25,
                    0, 0, 35, "Heals you and damages the enemy. Atk: 55, Heal: 40, Acc: 25"));
            add(new Skill("AccurateAttack", DmgType.UNSTRUCTURED, SkillGroup.ATTACK, 65, 0, 65,
                    0, 0, 25, "Attack which is more likely to hit the enemy. Atk: 65, Acc: 65"));
            add(new Skill("DefBoost", DmgType.UNSTRUCTURED, SkillGroup.SUPPORT, -100, 0, 60,
                    0, 20, 30, "Decreases damage on later incoming attacks. Acc: 60, DefBoost: 20"));
        }
    };

    public double typeMultiplier(DmgType opponentType){
        switch(this.type) {
            case OBJECT_ORIENTED:
                switch(opponentType){
                    case OBJECT_ORIENTED:
                        return 1.0;
                    case PROCEDURAL:
                        return 1.2;
                    case FUNCTIONAL:
                        return 0.8;
                    case UNSTRUCTURED:
                        return 1.0;
                }
            case PROCEDURAL:
                switch(opponentType){
                    case OBJECT_ORIENTED:
                        return 0.8;
                    case PROCEDURAL:
                        return 1.0;
                    case FUNCTIONAL:
                        return 1.2;
                    case UNSTRUCTURED:
                        return 1.0;
                }
            case FUNCTIONAL:
                switch(opponentType){
                    case OBJECT_ORIENTED:
                        return 1.2;
                    case PROCEDURAL:
                        return 0.8;
                    case FUNCTIONAL:
                        return 1.0;
                    case UNSTRUCTURED:
                        return 1.0;
                }
            case UNSTRUCTURED:
                switch(opponentType){
                    case OBJECT_ORIENTED:
                        return 1.0;
                    case PROCEDURAL:
                        return 1.0;
                    case FUNCTIONAL:
                        return 1.0;
                    case UNSTRUCTURED:
                        return 1.0;
                }
        }

        return 0;
    }
}
