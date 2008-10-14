/* Generated By:JJTree: Do not edit this line. ASTPeriodicGroupNumber.java */

package org.openscience.cdk.smiles.smarts.parser;

/**
 * An AST node. It represents the periodic group number of an element.
 *
 * This is not specified in the original Daylight specification, but
 * is support by MOE
 *
 * @author Rajarshi Guha
 * @cdk.created 2008-10-13
 * @cdk.module smarts
 * @cdk.svnrev  $Revision: 12556 $
 * @cdk.keyword SMARTS AST
 */
public class ASTPeriodicGroupNumber extends SimpleNode {

    /**
     * The periodic table group number.
     */
    private int groupNumber;

    /**
     * Get the periodic table group number for this element.
     *
     * @return the group number
     */
    public int getGroupNumber() {
        return groupNumber;
    }

    /**
     * Set the periodic table group number for this element.
     *
     * @param groupNumber the group number
     */
    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public ASTPeriodicGroupNumber(int id) {
    super(id);
  }

  public ASTPeriodicGroupNumber(SMARTSParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SMARTSParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}