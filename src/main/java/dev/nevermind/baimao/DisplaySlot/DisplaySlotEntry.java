package dev.nevermind.baimao.DisplaySlot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class DisplaySlotEntry {
    @Setter @Getter private String name;
    @Setter @Getter private String title;
    @Setter @Getter private int level;
    @Setter @Getter private String permission;
}
