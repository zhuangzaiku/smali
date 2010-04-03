/*
 * [The "BSD licence"]
 * Copyright (c) 2010 Ben Gruver (JesusFreke)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jf.baksmali.Adaptors.Format;

import org.jf.baksmali.IndentingPrintWriter;
import org.jf.dexlib.Code.Format.UnresolvedNullReference;
import org.jf.dexlib.CodeItem;

import java.io.IOException;

public class UnresolvedNullReferenceMethodItem extends InstructionMethodItem<UnresolvedNullReference> {
    public final boolean isLastInstruction;

    public UnresolvedNullReferenceMethodItem(CodeItem codeItem, int codeAddress, UnresolvedNullReference instruction,
                                             boolean isLastInstruction) {
        super(codeItem, codeAddress, instruction);
        this.isLastInstruction = isLastInstruction;
    }

    public boolean writeTo(IndentingPrintWriter writer) throws IOException {
        switch (instruction.OriginalInstruction.opcode)
        {
            case INVOKE_VIRTUAL_QUICK_RANGE:
            case INVOKE_SUPER_QUICK_RANGE:
                writeInvokeRangeTo(writer);
                return true;
            default:
                writeThrowTo(writer);
                return true;
        }
    }

    private void writeInvokeRangeTo(IndentingPrintWriter writer) throws IOException {
        writer.println("#Replaced unresolvable optimized invoke-*-range-quick instruction");
        writer.println("#with a generic method call that will throw a NullPointerException");
        writer.write("invoke-virtual/range {");
        writeRegister(writer, instruction.ObjectRegisterNum);
        writer.write(" .. ");
        writeRegister(writer, instruction.ObjectRegisterNum);
        writer.write("}, Ljava/lang/Object;->hashCode()I");
        if (isLastInstruction) {
            writer.println();
            writer.write("goto/32 0");
        }
    }

    private void writeThrowTo(IndentingPrintWriter writer) throws IOException {
        writer.println("#Replaced unresolvable optimized instruction with a throw");
        writer.write("throw ");
        writeRegister(writer, instruction.ObjectRegisterNum);
    }
}