package mp.evolution.scripts.fly;

import mp.evolution.game.camera.Camera;
import mp.evolution.game.camera.CameraType;
import mp.evolution.game.controls.Control;
import mp.evolution.game.controls.ControlGroup;
import mp.evolution.game.controls.Controls;
import mp.evolution.game.entity.ped.Ped;
import mp.evolution.math.Vector3;
import mp.evolution.script.Script;
import mp.evolution.script.event.ScriptEvent;
import mp.evolution.script.event.ScriptEventKeyboardKey;

public class Fly extends Script {
    private final Controls controls = new Controls(this);
    private Camera camera;
    private boolean shift, ctrl, down, up;

    @Override
    public void frame() {
        if (camera != null) {
            Vector3 pos = camera.getPosition();
            Vector3 rot = camera.getRotation(2);
            Vector3 dir = camera.getDirection();
            float speed = (shift ? 3 : 1) * (ctrl ? 0.5F : 1);
            float deltaHeading = controls.getDisabledNormal(ControlGroup.MOVE, Control.SCRIPT_RIGHT_AXIS_X);
            float deltaPitch = controls.getDisabledNormal(ControlGroup.MOVE, Control.SCRIPT_RIGHT_AXIS_Y);
            float moveSide = controls.getDisabledNormal(ControlGroup.MOVE, Control.SCRIPT_LEFT_AXIS_X);
            float moveFront = controls.getDisabledNormal(ControlGroup.MOVE, Control.SCRIPT_LEFT_AXIS_Y);
            float moveUp = (up ? 0.5F : 0) - (down ? 0.5F : 0);
            Vector3 velocity = dir.multiply(moveFront * speed);
            Vector3 up = new Vector3(0, 0, 1);
            Vector3 right = dir.cross(up).multiply(moveSide * 0.5F * speed);
            Ped player = Ped.local(this);
            player.setPositionNoOffset(pos.add(velocity).add(new Vector3(1, 1, 1)));
            player.setHeading(rot.z);
            camera.setPosition(pos.subtract(velocity).add(right).subtract(up.multiply(moveUp * speed)));
            camera.setRotation(
                rot.x + deltaPitch * -5F,
                0,
                rot.z + deltaHeading * -5F,
                2
            );
        }
    }

    @Override
    public boolean event(ScriptEvent event) {
        if (event instanceof ScriptEventKeyboardKey) {
            ScriptEventKeyboardKey e = (ScriptEventKeyboardKey) event;
            switch (e.key) {
                case F2:
                    if (!e.isUp) {
                        Ped player = Ped.local(this);
                        if (camera != null) {
                            player.setPositionNoOffset(camera.getPosition());
                            camera.destroy();
                            camera = null;
                            Camera.renderScripted(this, false);
                            //player.setPositionFreeze(false);
                            player.setInvincible(false);
                            player.setVisible(true);
                            player.setCollision(true, true);
                        } else {
                            Vector3 rot = Camera.Gameplay.getRotation(this, 2);
                            Vector3 pos = player.getPosition();
                            camera = new Camera(this, CameraType.DEFAULT_SCRIPTED, pos, rot, 45F);
                            camera.setActive(true);
                            Camera.renderScripted(this, true);
                            //player.setPositionFreeze(true);
                            player.setInvincible(true);
                            player.setVisible(false);
                            player.setCollision(false, false);
                        }
                    }
                    break;
                case SHIFT:
                    shift = !e.isUp;
                    break;
                case CONTROL:
                    ctrl = !e.isUp;
                    break;
                case KEY_Q:
                    up = !e.isUp;
                    break;
                case KEY_E:
                    down = !e.isUp;
                    break;
            }
        }
        return false;
    }
}
