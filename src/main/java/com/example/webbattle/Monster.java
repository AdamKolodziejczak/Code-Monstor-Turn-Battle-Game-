package com.example.webbattle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Monster {

    public String name;
    public DmgType type;
    public double health;
    public double maxHealth;
    public double stamina;
    public double maxStamina;
    public double defence;
    public double attack;
    public double accuracy;
    public List<Skill> skills;
    public String imageFile; // Add icon file name

    public int playerId = 0;

    public int id;
    private static int idCount = 0;

    // Parameterized constructor
    public Monster(String name, DmgType type, double maxHealth, double maxStamina, double defence,
                   double attack, double accuracy, List<Skill> skills, String imageFile){
        this.name = name;
        this.type = type;
        this.maxHealth = maxHealth;
        this.health = this.maxHealth;
        this.maxStamina = maxStamina;
        this.stamina = this.maxStamina;
        this.defence = defence;
        this.attack = attack;
        this.accuracy = accuracy;
        this.id = idCount++;
        this.skills = skills;
        this.imageFile = imageFile;
    }

    // Defines each of the types of monsters
    // Includes the name of their sprite image
    public enum MonsterKind {
        CPLUSPLUS("Cpp.png"),
        C("C.png"),
        JAVA("Java.png"),
        JAVASCRIPT("JavaScript.png"),
        RUST("Rust.png"),
        ASSEMBLY("Assembly.png"),
        PYTHON("Python.png");

        private final String imageFile;

        MonsterKind(String imageFile) {
            this.imageFile = imageFile;
        }

        public String getImageFile() {
            return imageFile;
        }
    }

    // Creates an instance of the monster with the given skills and stats
    public static Monster createMonster(MonsterKind type) {
        switch (type) {
            case CPLUSPLUS:
                return new Monster("C++", DmgType.OBJECT_ORIENTED, 200, 150,
                        45, 45, 40, new ArrayList<>(){
                    {
                        add(Skill.skills.get(0)); add(Skill.skills.get(1)); add(Skill.skills.get(2));
                        add(Skill.skills.get(9)); add(Skill.skills.get(12));
                    }
                }, type.getImageFile());
            case C:
                return new Monster("C", DmgType.PROCEDURAL, 150, 150,
                        50, 35, 35, new ArrayList<>(){
                    {
                        add(Skill.skills.get(0)); add(Skill.skills.get(3)); add(Skill.skills.get(4));
                        add(Skill.skills.get(6)); add(Skill.skills.get(11));
                    }
                }, type.getImageFile());
            case JAVA:
                return new Monster("Java", DmgType.OBJECT_ORIENTED, 225, 120,
                        45, 50, 30, new ArrayList<>(){
                    {
                        add(Skill.skills.get(1)); add(Skill.skills.get(3)); add(Skill.skills.get(5));
                        add(Skill.skills.get(7)); add(Skill.skills.get(8));
                    }
                }, type.getImageFile());
            case JAVASCRIPT:
                return new Monster("Javascript", DmgType.OBJECT_ORIENTED, 180, 120,
                        30, 50, 45, new ArrayList<>(){
                    {
                        add(Skill.skills.get(0)); add(Skill.skills.get(3)); add(Skill.skills.get(6));
                        add(Skill.skills.get(9)); add(Skill.skills.get(10));
                    }
                }, type.getImageFile());
            case RUST:
                return new Monster("Rust", DmgType.FUNCTIONAL, 250, 150,
                        35, 45, 50, new ArrayList<>(){
                    {
                        add(Skill.skills.get(4)); add(Skill.skills.get(5)); add(Skill.skills.get(7));
                        add(Skill.skills.get(8)); add(Skill.skills.get(11));
                    }
                }, type.getImageFile());
            case ASSEMBLY:
                return new Monster("x86 Assembly", DmgType.UNSTRUCTURED, 150, 150,
                        60, 40, 40, new ArrayList<>(){
                    {
                        add(Skill.skills.get(1)); add(Skill.skills.get(2)); add(Skill.skills.get(7));
                        add(Skill.skills.get(10)); add(Skill.skills.get(12));
                    }
                }, type.getImageFile());
            case PYTHON:
                return new Monster("Python", DmgType.PROCEDURAL, 175, 200,
                        50, 30, 45, new ArrayList<>(){
                    {
                        add(Skill.skills.get(2)); add(Skill.skills.get(5)); add(Skill.skills.get(6));
                        add(Skill.skills.get(10)); add(Skill.skills.get(11));
                    }
                }, type.getImageFile());
        }
        throw new RuntimeException();
    }

    // randomly selects 3 monsters and puts them on a team, does this for each user
    public static ArrayList<Monster> createMonsterList() {
        ArrayList<Monster> monsters = new ArrayList<>();
        Random random = new Random();

        // Generate three random monster types and add them to the list
        while (monsters.size() < 3) {
            MonsterKind randomMonsterKind = MonsterKind.values()[random.nextInt(MonsterKind.values().length)];
            Monster monster = createMonster(randomMonsterKind);
            boolean alreadyHas = false;
            for (Monster other : monsters) {
                if (other.name.equals(monster.name)) {
                    alreadyHas = true;
                    break;
                }
            }
            // ensures no duplicates are added to the same team
            if (!alreadyHas) {
                //monster.health /= 10;
                monsters.add(monster);
            }
        }

        return monsters;
    }
}
