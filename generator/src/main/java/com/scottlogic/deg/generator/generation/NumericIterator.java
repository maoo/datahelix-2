package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.generation.tmpReducerOutput.NumericRestrictions;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

class NumericIterator implements IFieldSpecIterator {
     private enum Strategy {
         UpFromMin,
         DownFromMax,
         MinToMax,
         OutFromMiddle
     }

     private final NumericRestrictions restrictions;
     private final Set<Object> blacklist;
     private Strategy strategy;
     private BigDecimal nextValue;
     private boolean hasNext = true;

     NumericIterator(NumericRestrictions restrictions, Set<Object> blacklist) {
         this.restrictions = restrictions;
         chooseStrategy();
         if (blacklist != null) {
             this.blacklist = blacklist;
         }
         else {
             this.blacklist = new HashSet<>();
         }
         setInitialValue();
     }

    @Override
    public Object next() {
         if (!hasNext) {
             return null;
         }
         BigDecimal rv = nextValue;
         hasNext = computeNextValue();
         return rv;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public boolean isInfinite() {
        return true;
    }

    private void chooseStrategy() {
         if (restrictions.min == null) {
             if (restrictions.max == null) {
                 strategy = Strategy.OutFromMiddle;
             }
             else {
                 strategy = Strategy.DownFromMax;
             }
         }
         else {
             if (restrictions.max == null) {
                 strategy = Strategy.UpFromMin;
             }
             else {
                 strategy = Strategy.MinToMax;
             }
         }
     }

     private void setInitialValue() {
         switch (strategy) {
             case OutFromMiddle:
                 nextValue = BigDecimal.ZERO;
                 break;
             case UpFromMin:
             case MinToMax:
                 nextValue = restrictions.min.getInclusive() ?
                         restrictions.min.getLimit() :
                         restrictions.min.getLimit().add(BigDecimal.ONE);
                 break;
             case DownFromMax:
                 nextValue = restrictions.max.getInclusive() ?
                         restrictions.max.getLimit() :
                         restrictions.max.getLimit().subtract(BigDecimal.ONE);
                 break;
         }
     }

     private boolean computeNextValue() {
         switch (strategy) {
             default:
             case UpFromMin:
                 nextValue = nextValue.add(BigDecimal.ONE);
                 return true;
             case MinToMax:
                 nextValue = nextValue.add(BigDecimal.ONE);
                 if ((nextValue.compareTo(restrictions.max.getLimit()) > 0) ||
                         (!restrictions.max.getInclusive() && nextValue.compareTo(restrictions.max.getLimit()) == 0)) {
                     return false;
                 }
                 return true;
             case DownFromMax:
                 nextValue = nextValue.subtract(BigDecimal.ONE);
                 return true;
             case OutFromMiddle:
                 if (nextValue.compareTo(BigDecimal.ZERO) > 0) {
                     nextValue = BigDecimal.ZERO.subtract(nextValue);
                 }
                 else {
                     nextValue = nextValue.abs().add(BigDecimal.ONE);
                 }
                 return true;
         }
     }
}
