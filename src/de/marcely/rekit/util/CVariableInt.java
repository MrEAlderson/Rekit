package de.marcely.rekit.util;

//variable int packing
public class CVariableInt
{
//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: static unsigned char *Pack(unsigned char *pDst, int i);

	// Format: ESDDDDDD EDDDDDDD EDD... Extended, Data, Sign
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on the parameter 'pDst', so pointers on this parameter are left unchanged:
//C++ TO JAVA CONVERTER WARNING: Java has no equivalent to methods returning pointers to value types:
//ORIGINAL LINE: static byte *Pack(byte *pDst, int i)
	public static byte Pack(byte pDst, int i)
	{
//C++ TO JAVA CONVERTER WARNING: The right shift operator was not replaced by Java's logical right shift operator since the left operand was not confirmed to be of an unsigned type, but you should review whether the logical right shift operator (>>>) is more appropriate:
		pDst = (byte) ((i >> 25) & 0x40); // set sign bit if i<0
//C++ TO JAVA CONVERTER WARNING: The right shift operator was not replaced by Java's logical right shift operator since the left operand was not confirmed to be of an unsigned type, but you should review whether the logical right shift operator (>>>) is more appropriate:
		i = i ^ (i >> 31); // if(i<0) i = ~i

		pDst |= i & 0x3F; // pack 6bit into dst
//C++ TO JAVA CONVERTER WARNING: The right shift operator was not replaced by Java's logical right shift operator since the left operand was not confirmed to be of an unsigned type, but you should review whether the logical right shift operator (>>>) is more appropriate:
		i >>= 6; // discard 6 bits
		if (i != 0)
		{
			pDst |= 0x80; // set extend bit
			while (true)
			{
				pDst++;
				pDst = (byte) (i & (0x7F)); // pack 7bit
//C++ TO JAVA CONVERTER WARNING: The right shift operator was not replaced by Java's logical right shift operator since the left operand was not confirmed to be of an unsigned type, but you should review whether the logical right shift operator (>>>) is more appropriate:
				i >>= 7; // discard 7 bits
				pDst |= (i != 0 ? i : 0) << 7; // set extend bit (may branch)
				if (i == 0)
				{
					break;
				}
			}
		}

		pDst++;
		return pDst;
	}
//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: static const unsigned char *Unpack(const unsigned char *pSrc, int *pInOut);
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on the parameter 'pSrc', so pointers on this parameter are left unchanged:
//C++ TO JAVA CONVERTER WARNING: Java has no equivalent to methods returning pointers to value types:
//ORIGINAL LINE: static const byte *Unpack(const byte *pSrc, int *pInOut)
	public static byte Unpack(byte pSrc)
	{
		RefObject<Integer> pInOut = new RefObject<>(0);
//C++ TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift operator since the left operand was originally of an unsigned type, but you should confirm this replacement:
		int Sign = (pSrc>>>6) & 1;
		pInOut.argValue = pSrc & 0x3F;

		do
		{
			if (((pSrc & 0x80) == 0))
			{
				break;
			}
			pSrc++;
			pInOut.argValue |= (pSrc & (0x7F)) << (6);

			if (((pSrc & 0x80) == 0))
			{
				break;
			}
			pSrc++;
			pInOut.argValue |= (pSrc & (0x7F)) << (6 + 7);

			if (((pSrc & 0x80) == 0))
			{
				break;
			}
			pSrc++;
			pInOut.argValue |= (pSrc & (0x7F)) << (6 + 7 + 7);

			if (((pSrc & 0x80) == 0))
			{
				break;
			}
			pSrc++;
			pInOut.argValue |= (pSrc & (0x7F)) << (6 + 7 + 7 + 7);
		} while (0 != 0);

		pSrc++;
		pInOut.argValue ^= -Sign; // if(sign) *i = ~(*i)
		return pSrc;
	}
	public static int Compress(Object pSrc_, int Size, Object pDst_)
	{
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged:
		int pSrc = (int)pSrc_;
	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: unsigned char *pDst = (unsigned char *)pDst_;
//C++ TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to pointers to value types:
//ORIGINAL LINE: byte *pDst = (byte *)pDst_;
		byte pDst = (byte)pDst_;
		Size /= 4;
		while (Size != 0)
		{
			pDst = CVariableInt.Pack(pDst, pSrc);
			Size--;
			pSrc++;
		}
	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: return (long)(pDst-(unsigned char *)pDst_);
		return (int)(pDst - (byte)pDst_);
	}
	public static int Decompress(Object pSrc_, int Size, Object pDst_)
	{
	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: const unsigned char *pSrc = (unsigned char *)pSrc_;
//C++ TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to pointers to value types:
//ORIGINAL LINE: const byte *pSrc = (byte *)pSrc_;
		byte pSrc = (byte)pSrc_;
	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: const unsigned char *pEnd = pSrc + Size;
//C++ TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to pointers to value types:
//ORIGINAL LINE: const byte *pEnd = pSrc + Size;
		byte pEnd = (byte) (pSrc + Size);
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged:
		int pDst = (int)pDst_;
		while (pSrc < pEnd)
		{
			RefObject<Integer> tempRef_pDst = new RefObject<Integer>(pDst);
			pSrc = CVariableInt.Unpack(pSrc);
			pDst = tempRef_pDst.argValue;
			pDst++;
		}
	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: return (long)((unsigned char *)pDst-(unsigned char *)pDst_);
		return (int)((byte)pDst - (byte)pDst_);
	}
	
	//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2018 Tangible Software Solutions Inc.
//	This class can be used by anyone provided that the copyright notice remains intact.
//
//	This class is used to replicate the ability to pass arguments by reference in Java.
//----------------------------------------------------------------------------------------
public static class RefObject<T>
{
	public T argValue;
	public RefObject(T refArg)
	{
		argValue = refArg;
	}
}
}