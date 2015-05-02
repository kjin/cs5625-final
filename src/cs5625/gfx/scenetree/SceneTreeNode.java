/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  Copyright (c) 2015, Department of Computer Science, Cornell University.
 *
 *  This code repository has been authored collectively by:
 *  Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

package cs5625.gfx.scenetree;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cs5625.gfx.json.AbstractNamedObject;
import cs5625.gfx.json.JsonUtil;
import cs5625.gfx.json.NamedObject;
import cs5625.gfx.objcache.Holder;
import cs5625.util.VectorMathUtil;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class SceneTreeNode extends AbstractNamedObject {
    /* Attributes common to all SceneGraphNode subclasses. */
    private Point3f position = new Point3f();
    private Quat4f orientation = new Quat4f(0.0f, 0.0f, 0.0f, 1.0f);
    private float scale = 1.0f;
    private SceneTreeNode parent = null;
    /* The data that is held in this node */
    private Holder<NamedObject> data;

    /* List of child nodes. */
    private ArrayList<SceneTreeNode> children = new ArrayList<SceneTreeNode>();

    public Holder<NamedObject> getData() {
        return data;
    }

    public SceneTreeNode setData(Holder<NamedObject> data) {
        this.data = data;
        return this;
    }

    /**
     * Returns this object's parent node, if any.
     */
    public SceneTreeNode getParent() {
        return parent;
    }

    /**
     * Returns all direct child nodes of this node.
     */
    public List<SceneTreeNode> getChildren() {
        return children;
    }

    /**
     * Adds a new child node to this node.
     */
    public SceneTreeNode addChild(SceneTreeNode child) {
        if (child.parent != null) {
            throw new RuntimeException("Cannot add child to multiple parents.");
        }
        children.add(child);
        child.parent = this;
        return this;
    }

    /**
     * Adds new children nodes to this node.
     */
    public SceneTreeNode addChildren(Iterable<SceneTreeNode> children) {
        for (SceneTreeNode child : children) {
            addChild(child);
        }
        return this;
    }

    /**
     * Removes a child node from this node.
     */
    public SceneTreeNode removeChild(SceneTreeNode child) {
        if (child.parent != this) {
            throw new RuntimeException("Object to remove doesn't have parent of this node.");
        }
        if (!children.remove(child)) {
            throw new RuntimeException("Object to remove is not a child of this ScenegraphObject.");
        }
        child.parent = null;
        return this;
    }

    /**
     * Removes children from this node.
     */
    public SceneTreeNode removeChildren(Iterable<SceneTreeNode> children) throws RuntimeException {
        for (SceneTreeNode child : children) {
            removeChild(child);
        }
        return this;
    }

    /**
     * Returns the first child of this object with the given name, or null if no child has that name.
     */
    public SceneTreeNode findChildByName(String name) {
        for (SceneTreeNode child : children) {
            if (child.getName().equals(name)) {
                return child;
            }
        }

        return null;
    }

    /**
     * Returns the first descendant of this object with the given name, or null if no descendant has that name.
     * Search order is pre-order.
     */
    public SceneTreeNode findDescendantByName(String name) {
        for (SceneTreeNode child : children) {
            if (child.getName().equals(name)) {
                return child;
            }
            SceneTreeNode descendant = child.findDescendantByName(name);
            if (descendant != null) {
                return descendant;
            }
        }
        return null;
    }

    /**
     * Returns the position of this object in its parent's space.
     */
    public Point3f getPosition() {
        return position;
    }

    /**
     * Sets the position of this object in its parent's space.
     */
    public SceneTreeNode setPosition(Point3f position) {
        this.position.set(position);
        return this;
    }

    public SceneTreeNode setPosition(float x, float y, float z) {
        this.position.set(x,y,z);
        return this;
    }

    /**
     * Returns the orientation of this object in its parent's space.
     */
    public Quat4f getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation of this object in its parent's space.
     */
    public SceneTreeNode setOrientation(Quat4f orientation) {
        this.orientation.set(orientation);
        return this;
    }

    /**
     * Returns the scale of this object in its parent's space.
     */
    public float getScale() {
        return scale;
    }

    /**
     * Sets the scale of this object in its parent's space.
     */
    public SceneTreeNode setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public void getToParentSpaceMatrix(Matrix4f M) {
        M.setIdentity();
        M.setRotation(orientation);
        M.setScale(scale);
        M.setTranslation(new Vector3f(position));
    }

    public void getToWorldSpaceMatrix(Matrix4f M) {
        if (parent == null) {
            getToParentSpaceMatrix(M);
        } else {
            Matrix4f A = new Matrix4f();
            Matrix4f B = new Matrix4f();
            parent.getToWorldSpaceMatrix(A);
            getToParentSpaceMatrix(B);
            M.mul(A, B);
        }
    }

    /**
     * Transforms a point in this object's local space into its parent's space.
     */
    public Point3f transformPointToParentSpace(Point3f point) {
        Point3f result = new Point3f(point);

        result.scale(scale);
        VectorMathUtil.rotateTuple(orientation, result);
        result.add(position);

        return result;
    }

    /**
     * Transforms a point in this object's parent's space into its local space.
     */
    public Point3f transformPointFromParentSpace(Point3f point) {
        Quat4f invOrientation = new Quat4f();
        invOrientation.inverse(orientation);

        Point3f result = new Point3f(point);

        result.sub(position);
        VectorMathUtil.rotateTuple(invOrientation, result);
        result.scale(1.0f / scale);

        return result;
    }

    /**
     * Transforms a point in this object's local space into world space.
     */
    public Point3f transformPointToWorldSpace(Point3f point) {
        if (parent == null) {
            return transformPointToParentSpace(point);
        } else {
            return parent.transformPointToWorldSpace(transformPointToParentSpace(point));
        }
    }

    /**
     * Transforms a point in world space into this object's local space.
     */
    public Point3f transformPointFromWorldSpace(Point3f point) {
        if (parent == null) {
            return transformPointFromParentSpace(point);
        } else {
            return transformPointFromParentSpace(parent.transformPointFromWorldSpace(point));
        }
    }

    /**
     * Transforms a direction in this object's local space into its parent's space.
     */
    public Vector3f transformVectorToParentSpace(Vector3f direction) {
        Vector3f result = new Vector3f(direction);

        result.scale(scale);
        VectorMathUtil.rotateTuple(orientation, result);

        return result;
    }

    /**
     * Transforms a direction in this object's parent's space into its local space.
     */
    public Vector3f transformVectorFromParentSpace(Vector3f direction) {
        Quat4f invOrientation = new Quat4f();
        invOrientation.inverse(orientation);

        Vector3f result = new Vector3f(direction);

        VectorMathUtil.rotateTuple(invOrientation, result);
        result.scale(1.0f / scale);

        return result;
    }

    /**
     * Transforms a direction in this object's local space into world space.
     */
    public Vector3f transformVectorToWorldSpace(Vector3f direction) {
        if (parent == null) {
            return transformVectorToParentSpace(direction);
        } else {
            return parent.transformVectorToWorldSpace(transformVectorToParentSpace(direction));
        }
    }

    /**
     * Transforms a direction in world space into this object's local space.
     */
    public Vector3f transformVectorFromWorldSpace(Vector3f direction) {
        if (parent == null) {
            return transformVectorFromParentSpace(direction);
        } else {
            return transformVectorFromParentSpace(parent.transformVectorFromWorldSpace(direction));
        }
    }

    /**
     * Transforms a distance in this object's local space into its parent's space.
     */
    public float transformDistanceToParentSpace(float distance) {
        return distance * scale;
    }

    /**
     * Transforms a distance in this object's parent's space to its local space.
     */
    public float transformDistanceFromParentSpace(float distance) {
        return distance / scale;
    }

    /**
     * Transforms a distance in this object's local space into world space.
     */
    public float transformDistanceToWorldSpace(float distance) {
        if (parent == null) {
            return transformDistanceToParentSpace(distance);
        } else {
            return parent.transformDistanceToWorldSpace(transformDistanceToParentSpace(distance));
        }
    }

    /**
     * Transforms a distance in world space into this object's local space.
     */
    public float transformDistanceFromWorldSpace(float distance) {
        if (parent == null) {
            return transformDistanceFromParentSpace(distance);
        } else {
            return transformDistanceFromParentSpace(parent.transformDistanceFromWorldSpace(distance));
        }
    }

    /**
     * Transforms an orientation in this object's local space into its parent's space.
     */
    public Quat4f transformOrientationToParentSpace(Quat4f orientation) {
        Quat4f result = new Quat4f(orientation);
        result.mul(orientation);
        return result;
    }

    /**
     * Transforms an orientation in this object's parent's space into its local space.
     */
    public Quat4f transformOrientationFromParentSpace(Quat4f orientation) {
        Quat4f result = new Quat4f();
        result.inverse(orientation);
        result.mul(orientation);
        return result;
    }

    /**
     * Transforms an orientation in this object's local space into world space.
     */
    public Quat4f transformOrientationToWorldSpace(Quat4f orientation) {
        if (parent == null) {
            return transformOrientationToParentSpace(orientation);
        } else {
            return parent.transformOrientationToWorldSpace(transformOrientationToParentSpace(orientation));
        }
    }

    /**
     * Transforms an orientation in world space into this object's local space.
     */
    public Quat4f transformOrientationFromWorldSpace(Quat4f orientation) {
        if (parent == null) {
            return transformOrientationFromParentSpace(orientation);
        } else {
            return transformOrientationFromParentSpace(parent.transformOrientationFromWorldSpace(orientation));
        }
    }

    /**
     * Let a SceneGraphTraverser traverse the tree.
     *
     * @param traverser
     */
    public SceneTreeNode letTraverse(SceneTreeTraverser traverser) {
        traverser.processNodeBeforeChildren(this);
        for (SceneTreeNode child : children) {
            child.letTraverse(traverser);
        }
        traverser.processNodeAfterChildren(this);
        return this;
    }

    @Override
    protected void fillJson(JsonObject json, String directory) {
        json.add("position", JsonUtil.toJson(position));
        json.add("orientation", JsonUtil.toJson(orientation));
        json.addProperty("scale", scale);
        if (data != null)
            json.add("data", data.toJson(directory));
        JsonArray childrenArray = new JsonArray();
        for (SceneTreeNode child : children) {
            JsonObject childObject = child.toJson(directory);
            childrenArray.add(childObject);
        }
        json.add("children", childrenArray);
    }

    @Override
    public void fromJson(JsonObject json, String directory) {
        super.fromJson(json, directory);
        JsonUtil.fromJson(json.get("position").getAsJsonArray(), position);
        JsonUtil.fromJson(json.get("orientation").getAsJsonArray(), orientation);
        scale = json.get("scale").getAsFloat();
        data = (Holder<NamedObject>)JsonUtil.fromJson(json.get("data"), directory);
        JsonArray childrenArray = json.get("children").getAsJsonArray();
        for (int i = 0; i < childrenArray.size(); i++) {
            JsonObject childObject = childrenArray.get(i).getAsJsonObject();
            SceneTreeNode child = (SceneTreeNode)JsonUtil.fromJson(childObject, directory);
            addChild(child);
        }
    }
}
